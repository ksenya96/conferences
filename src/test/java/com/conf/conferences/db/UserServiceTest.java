package com.conf.conferences.db;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TestEntityManager testEntityManager;

    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {

        @Bean
        public UserService userService() {
            return new UserService();
        }
    }

    @Test
    void loadUserByUsername_Ok() {
        saveUserWithPassword();
        UserDetails userDetails = userService.loadUserByUsername("test");
        Assert.assertEquals("test", userDetails.getUsername());
        Assert.assertEquals("123", userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_Fail() {
        saveUserWithPassword();
        UsernameNotFoundException exception = Assert.assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername("formago"));

        Assert.assertEquals("User not found with username: formago", exception.getMessage());
    }

    @Test
    void loadUserByUsernameAndOauth2Resource_Ok() {
        saveOauth2User();
        UserDetails userDetails = userService.loadUserByUsernameAndOauth2Resource("test", SocialType.GITHUB);
        Assert.assertEquals("test", userDetails.getUsername());
        Assert.assertNull(userDetails.getPassword());
    }

    @Test
    void loadUserByUsernameAndOauth2Resource_InvalidResource() {
        saveOauth2User();
        UsernameNotFoundException exception = Assert.assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsernameAndOauth2Resource("test", SocialType.FACEBOOK));

        Assert.assertEquals("User not found with username: test from resource: FACEBOOK", exception.getMessage());
    }

    @Test
    void loadUserByUsernameAndOauth2Resource_InvalidUsername() {
        saveOauth2User();
        UsernameNotFoundException exception = Assert.assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsernameAndOauth2Resource("formago", SocialType.GITHUB));

        Assert.assertEquals("User not found with username: formago from resource: GITHUB", exception.getMessage());
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