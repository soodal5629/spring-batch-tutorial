package com.example.springbatchtutorial.job.multiplestep;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
//@RequiredArgsConstructor
public class MultipleStepJobConfig {
    @Bean
    public Job multipleStepJob(JobRepository jobRepository, Step multipleStep1, Step multipleStep2, Step multipleStep3) {
        return new JobBuilder("multipleStepJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(multipleStep1)
                .next(multipleStep2)
                .next(multipleStep3)
                .build();
    }
    @Bean
    public Step multipleStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet validatedParamTasklet) {
        return new StepBuilder("multipleStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("###### step1 ######");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
    @Bean
    public Step multipleStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet validatedParamTasklet) {
        return new StepBuilder("multipleStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("###### step2 ######");
                    ExecutionContext executionContext = chunkContext
                                    .getStepContext()
                                    .getStepExecution()
                                    .getJobExecution()
                                    .getExecutionContext();
    executionContext.put("key", "hello!!"); // 다른 step에서 해당 데이터 사용 가능
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step multipleStep3(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet validatedParamTasklet) {
        return new StepBuilder("multipleStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("###### step3 ######");
                    ExecutionContext executionContext = chunkContext
                                    .getStepContext()
                                    .getStepExecution()
                                    .getJobExecution()
                                    .getExecutionContext();
                    log.info("###### step2로부터 전달 받은 데이터: {}", executionContext.get("key"));
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
