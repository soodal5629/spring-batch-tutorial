package com.example.springbatchtutorial.job.conditionalstep;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
/**
 * 상황/조건에 따라 Step 분기 처리
 */
public class ConditionalStepJobConfig {
    @Bean
    public Job conditionalStepJob(JobRepository jobRepository, Step conditionalStartStep, Step conditionalFailStep
            , Step conditionalCompletedStep, Step conditionalAllstep) {
        return new JobBuilder("conditionalStepJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(conditionalStartStep)
                    .on("FAILED").to(conditionalFailStep)
                .from(conditionalStartStep)
                    .on("COMPLETED").to(conditionalCompletedStep)
                .from(conditionalStartStep)
                    .on("*").to(conditionalAllstep)
                .end()
                .build();
    }
    @Bean
    public Step conditionalStartStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet validatedParamTasklet) {
        return new StepBuilder("conditionalStartStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("###### conditionalStartStep ######");
                    return RepeatStatus.FINISHED; // --> conditionalCompletedstep 작동
                    //throw new Exception("Exception"); --> FailStep 작동
                }, transactionManager)
                .build();
    }

    @Bean
    public Step conditionalAllstep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet validatedParamTasklet) {
        return new StepBuilder("conditionalAllstep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("###### conditionalAllstep ######");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step conditionalFailStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet validatedParamTasklet) {
        return new StepBuilder("conditionalFailStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("###### conditionalFailStep ######");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step conditionalCompletedStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet validatedParamTasklet) {
        return new StepBuilder("conditionalCompletedStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("###### conditionalCompletedStep ######");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
