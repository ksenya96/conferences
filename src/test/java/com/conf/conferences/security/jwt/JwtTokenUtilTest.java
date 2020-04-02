package com.conf.conferences.security.jwt;

import com.conf.conferences.db.User;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest()
class JwtTokenUtilTest {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String token;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setUsername("maxim");
        user.setPassword("Goliye_baby2020");
        token = jwtTokenUtil.generateToken(user);
    }

    @Test
    void getUsernameFromToken() {
        Assert.assertEquals("maxim", jwtTokenUtil.getUsernameFromToken(token));
    }

    @Test
    void getExpirationDateFromToken() {
        Assert.assertTrue(jwtTokenUtil.getExpirationDateFromToken(token).after(new Date()));
    }

    @Test
    void generateToken() {
        User user = new User();
        user.setUsername("lapchuk");
        Assert.assertTrue(jwtTokenUtil.generateToken(user).startsWith("ey"));
    }

    @Test
    void validateToken_Ok() {
        User user = new User();
        user.setUsername("maxim");
        user.setPassword("Goliye_baby2020");
        Assert.assertTrue(jwtTokenUtil.validateToken(token, user));
    }

    @Test
    void validateToken_Fail() {
        User user = new User();
        user.setUsername("formago");
        user.setPassword("Goliye_baby2020");
        Assert.assertFalse(jwtTokenUtil.validateToken(token, user));
    }
}