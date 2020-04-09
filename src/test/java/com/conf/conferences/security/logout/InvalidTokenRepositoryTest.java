package com.conf.conferences.security.logout;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class InvalidTokenRepositoryTest {

    @Autowired
    private InvalidTokenRepository invalidTokenRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void getByToken_Ok() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        InvalidToken invalidToken = new InvalidToken();
        invalidToken.setToken("asd456");
        invalidToken.setExpirationTime(nowDateTime);

        testEntityManager.persistAndFlush(invalidToken);
        InvalidToken test = invalidTokenRepository.getByToken("asd456");
        Assert.assertEquals("asd456", test.getToken());
        Assert.assertEquals(nowDateTime, test.getExpirationTime());
    }

    @Test
    void getByToken_Fail() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        InvalidToken invalidToken = new InvalidToken();
        invalidToken.setToken("asd456");
        invalidToken.setExpirationTime(nowDateTime);

        testEntityManager.persistAndFlush(invalidToken);
        Assert.assertNull(invalidTokenRepository.getByToken("456"));
    }

    @Test
    void saveAndFlush_Ok() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        InvalidToken invalidToken = new InvalidToken();
        invalidToken.setToken("asd456");
        invalidToken.setExpirationTime(nowDateTime);

        InvalidToken saved = invalidTokenRepository.saveAndFlush(invalidToken);
        Assert.assertTrue(saved.getTokenId() > 0);
    }

    @Test
    void saveAndFlush_Fail() {
        InvalidToken invalidToken = new InvalidToken();
        invalidToken.setToken("asd456");

        Assert.assertThrows(Exception.class, () -> invalidTokenRepository.saveAndFlush(invalidToken));
    }

    @Test
    void getAllByExpirationTimeBefore() {
        InvalidToken invalidToken1 = new InvalidToken();
        invalidToken1.setToken("asd456");
        invalidToken1.setExpirationTime(LocalDateTime.now().minusHours(3));

        InvalidToken invalidToken2 = new InvalidToken();
        invalidToken2.setToken("kotovich");
        invalidToken2.setExpirationTime(LocalDateTime.now().plusHours(3));

        testEntityManager.persistAndFlush(invalidToken1);
        testEntityManager.persistAndFlush(invalidToken2);

        List<InvalidToken> tokens = invalidTokenRepository.getAllByExpirationTimeBefore(LocalDateTime.now());
        Assert.assertEquals("asd456", tokens.get(0).getToken());
        Assert.assertEquals(1, tokens.size());
    }

    @Test
    void deleteAll() {
        InvalidToken invalidToken1 = new InvalidToken();
        invalidToken1.setToken("asd456");
        invalidToken1.setExpirationTime(LocalDateTime.now());

        InvalidToken invalidToken2 = new InvalidToken();
        invalidToken2.setToken("kotovich");
        invalidToken2.setExpirationTime(LocalDateTime.of(2020, 4, 9, 17, 35, 15));

        testEntityManager.persistAndFlush(invalidToken1);
        testEntityManager.persistAndFlush(invalidToken2);

        invalidTokenRepository.deleteAll(Arrays.asList(invalidToken1, invalidToken2));

    }
}