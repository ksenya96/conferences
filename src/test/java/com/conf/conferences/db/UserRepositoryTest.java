package com.conf.conferences.db;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void findByUsername_Ok() {
        saveUserWithPassword();

        UserDetails get = userRepository.findByUsername("test");

        Assert.assertEquals("123", get.getPassword());
        Assert.assertEquals("test", get.getUsername());
    }

    @Test
    void findByUsername_NotFound() {

        saveUserWithPassword();

        UserDetails get = userRepository.findByUsername("lapchuck");

        Assert.assertNull(get);
    }

    @Test
    void findByUsernameAndOauth2Resource_Ok() {
        saveOauth2User();
        UserDetails get = userRepository.findByUsernameAndOauth2Resource("test", SocialType.GITHUB);
        Assert.assertEquals("test", get.getUsername());
        Assert.assertNull(get.getPassword());
    }

    @Test
    void findByUsernameAndOauth2Resource_InvalidResource() {
        saveOauth2User();
        UserDetails get = userRepository.findByUsernameAndOauth2Resource("test", SocialType.GOOGLE);
        Assert.assertNull(get);
    }

    @Test
    void findByUsernameAndOauth2Resource_InvalidUsername() {
        saveOauth2User();
        UserDetails get = userRepository.findByUsernameAndOauth2Resource("formago", SocialType.GITHUB);
        Assert.assertNull(get);
    }

    private void saveUserWithPassword() {
        User user = new User();
        user.setName("Test");
        user.setUsername("test");
        user.setPassword("123");
        user.setEmail("test@test.com");
        testEntityManager.persistAndFlush(user);
    }

    private void saveOauth2User() {
        User user = new User();
        user.setName("Test");
        user.setUsername("test");
        user.setEmail("test@test.com");
        user.setOauth2Resource(SocialType.GITHUB);
        testEntityManager.persistAndFlush(user);
    }
}