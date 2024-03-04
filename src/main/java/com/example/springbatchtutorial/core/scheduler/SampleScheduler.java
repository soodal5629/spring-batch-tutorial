package com.example.springbatchtutorial.core.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SampleScheduler {
    private final JobLauncher jobLauncher; // 스케쥴링 기능 이용할 때 필요
    private final Job conditionalStepJob;

    @Scheduled(cron = "0/2 * * * * *") // 2초에 한번씩 실행
    public void conditionalStepJobRun() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("requestTime", System.currentTimeMillis())
                        .toJobParameters();
        jobLauncher.run(conditionalStepJob, jobParameters); // 파라미터가 없으면 동일한 job이라고 판단하기 때문에 job 실행이 안됨
    }

}
