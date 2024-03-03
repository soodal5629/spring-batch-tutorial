package com.example.springbatchtutorial.job.dbrw;

import com.example.springbatchtutorial.core.domain.accounts.AccountsRepository;
import com.example.springbatchtutorial.core.domain.orders.Orders;
import com.example.springbatchtutorial.core.domain.orders.OrdersRepository;
import com.example.springbatchtutorial.SpringBatchTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBatchTest
@SpringBootTest(classes = {SpringBatchTestConfig.class, TrMigrationConfig.class })
class TrMigrationConfigTest {

    /**
     * 중요!!!!!
     * Spring Batch에서는 기본 설정이 @Transactional 을 허용하지 않기 떄문에 테스트 코드에서 사용할 경우 에러가 나고
     * @BeforEach/@AfterEach 등을 통해 전체 데이터를 깔끔하게 관리해줘야 한다
     */

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    AccountsRepository accountsRepository;

    @AfterEach
    void after() {
        ordersRepository.deleteAll();
        accountsRepository.deleteAll();
    }

    /**
     * Data 없을 때
     */
    @Test
    void successNoData() throws Exception {
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(); // 실행에 대한 결과를 가지고 있음

        // then
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(accountsRepository.count()).isEqualTo(0);

    }

    /**
     * 데이터 있을 때
     */
    @Test
    public void successExistDate() throws Exception {
        // given
        Orders order1 = new Orders(null, "kakao gift", 15000, LocalDateTime.now());
        Orders order2 = new Orders(null, "naver gift", 15000, LocalDateTime.now());

        ordersRepository.save(order1);
        ordersRepository.save(order2);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(accountsRepository.count()).isEqualTo(2);
    }
}