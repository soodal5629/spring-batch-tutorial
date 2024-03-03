package com.example.springbatchtutorial.job.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobListenerConfig {

    @Bean
    public Job jobListenerJob(JobRepository jobRepository, Step jobListenerStep) {
        return new JobBuilder("jobListenerJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener()) // 리스너 추가
                .start(jobListenerStep)
                .build();
    }

    @Bean
    public Step jobListenerStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet jobListenerTasklet) {
        return new StepBuilder("jobListenerStep", jobRepository)
                .tasklet(jobListenerTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet jobListenerTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//                log.info("########## Job ListenerTasklet ##########");
//                return RepeatStatus.FINISHED;
                throw new Exception("after job listener 확인하기 위한 고의 exception");
            }
        };
    }
}
