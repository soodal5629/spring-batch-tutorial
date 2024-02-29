package com.example.springbatchtutorial.job.validatorparam;

import com.example.springbatchtutorial.job.validatorparam.validator.FileParamValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@Configuration
//@RequiredArgsConstructor
@Slf4j
public class ValidatedParamJobConfig {

    @Bean
    public Job validatedParamJob(JobRepository jobRepository, Step validatedParamStep) {
        return new JobBuilder("validatedParamJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                //.validator(new FileParamValidator())
                .validator(multipleValidator()) // 다수의 validator 등록 가능
                .start(validatedParamStep)
                .build();
    }

    private CompositeJobParametersValidator multipleValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(new FileParamValidator()));
        return validator;
    }

    @Bean
    public Step validatedParamStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet validatedParamTasklet) {
        return new StepBuilder("validatedParamStep", jobRepository)
                .tasklet(validatedParamTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope // jobParameters를 받고싶을때는 얘가 필요하고
    // program arguments에는 --job.name=validatedParamJob fileName=test.csv 라고 입력해줘야 함
    public Tasklet validatedParamTasklet(@Value("#{jobParameters['fileName']}") String fileName) {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("########## fileName: {}", fileName);
                log.info("########## validated Param Tasklet ##########");
                return RepeatStatus.FINISHED;
            }
        };
    }
}
