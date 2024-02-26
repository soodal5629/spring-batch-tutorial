package com.example.springbatchtutorial.job.dbrw;

import com.example.springbatchtutorial.core.domain.accounts.Accounts;
import com.example.springbatchtutorial.core.domain.accounts.AccountsRepository;
import com.example.springbatchtutorial.core.domain.orders.Orders;
import com.example.springbatchtutorial.core.domain.orders.OrdersRepository;
import com.example.springbatchtutorial.job.listener.JobLoggerListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Collections;

/**
 * 주문 테이블 -> 정산 테이블 데이터 이관
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {
    private final OrdersRepository ordersRepository;
    private final AccountsRepository accountsRepository;

    @Bean
    public Job trMigrationJob(JobRepository jobRepository, Step trMigrationStep) {
        return new JobBuilder("trMigrationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(trMigrationStep)
                .build();
    }

    @Bean
    public Step trMigrationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemReader trOrderReader
    , ItemProcessor trOrderProcessor, RepositoryItemWriter trOrderWriter) {
        return new StepBuilder("trMigrationStep", jobRepository)
                .<Orders, Accounts>chunk(5, transactionManager) // 5개의 데이터 단위로 처리 (5개의 트랜잭션)
                .reader(trOrderReader)
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(Chunk chunk) throws Exception {
//                        chunk.forEach(System.out::println);
//                    }
//                })
                .processor(trOrderProcessor) // 가공
                .writer(trOrderWriter)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemWriter<Accounts> trOrderWriter() {
        return new RepositoryItemWriterBuilder<Accounts>()
                .repository(accountsRepository)
                .methodName("save")
                .build();
    }

    // RepositoryItemWriter 말고 해당 방식도 가능
    @Bean
    @StepScope
    public ItemWriter<Accounts> trOrderWriter2() {
        return new ItemWriter<Accounts>() {
            @Override
            public void write(Chunk<? extends Accounts> chunk) throws Exception {
                chunk.forEach(item -> accountsRepository.save(item));
            }
        };
    }
    @Bean
    @StepScope
    public ItemProcessor<Orders, Accounts> trOrderProcessor() {
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                return new Accounts(item);
            }
        };
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Orders> trOrderReader() {
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrderReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5) // 보통 chunk size와 동일하게 작성
                .arguments(Arrays.asList()) // 파라미터가 없기 때문에 빈 배열 넘겨줌
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC)) // 정렬
                .build();
    }
}
