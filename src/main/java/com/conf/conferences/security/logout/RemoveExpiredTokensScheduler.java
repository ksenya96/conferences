package com.conf.conferences.security.logout;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class RemoveExpiredTokensScheduler {

    private InvalidTokenRepository invalidTokenRepository;

    @Autowired
    public void setInvalidTokenRepository(InvalidTokenRepository invalidTokenRepository) {
        this.invalidTokenRepository = invalidTokenRepository;
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void removeExpiredTokens() {
        log.info("Scheduler removes invalid expired token from DB");
        invalidTokenRepository.deleteAll(invalidTokenRepository.getAllByExpirationTimeBefore(LocalDateTime.now()));
    }
}
