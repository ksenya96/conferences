package com.conf.conferences.security.logout;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class RemoveExpiredTokensSchedulerTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private RemoveExpiredTokensScheduler removeExpiredTokensScheduler;

    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {

        @Bean
        public RemoveExpiredTokensScheduler removeExpiredTokensScheduler() {
            return new RemoveExpiredTokensScheduler();
        }
    }

    @Test
    void removeExpiredTokens() {
        InvalidToken invalidToken1 = new InvalidToken();
        invalidToken1.setToken("asd456");
        invalidToken1.setExpirationTime(LocalDateTime.now().minusHours(3));

        InvalidToken invalidToken2 = new InvalidToken();
        invalidToken2.setToken("kotovich");
        invalidToken2.setExpirationTime(LocalDateTime.now().plusHours(3));

        testEntityManager.persistAndFlush(invalidToken1);
        testEntityManager.persistAndFlush(invalidToken2);

        removeExpiredTokensScheduler.removeExpiredTokens();

    }
}