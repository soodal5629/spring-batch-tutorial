package com.example.springbatchtutorial.job.validatorparam;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBatchTest
@SpringBootTest(classes = {SpringBatchTestConfig.class, ValidatedParamJobConfig.class})
class ValidatedParamJobConfigTest {

    @Autowired JobLauncherTestUtils jobLauncherTestUtils;
    @Test
    void success() throws Exception {
        JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
                .addString("fileName", "test.csv")
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    }
}