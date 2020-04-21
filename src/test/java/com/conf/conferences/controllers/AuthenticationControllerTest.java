package com.conf.conferences.controllers;

import com.conf.conferences.ConferencesApplication;
import com.conf.conferences.db.User;
import com.conf.conferences.db.UserDto;
import com.conf.conferences.db.UserService;
import com.conf.conferences.security.jwt.JwtRequest;
import com.conf.conferences.security.jwt.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConferencesApplication.class)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthenticationController authenticationController;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    UserService userDetailsService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Before
    public void setup() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6");

        when(userDetailsService.loadUserByUsername("username@gmail.com")).thenReturn(user);
        when(userDetailsService.loadUserByUsername(not(eq("username@gmail.com"))))
                .thenReturn(null);

        UsernamePasswordAuthenticationToken authenticationTokenReturn =
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        when(authenticationManager.authenticate(eq(new UsernamePasswordAuthenticationToken("username@gmail.com", "password"))))
                .thenReturn(authenticationTokenReturn);
        when(authenticationManager.authenticate(not(eq(new UsernamePasswordAuthenticationToken("username@gmail.com", "password")))))
                .thenThrow(new BadCredentialsException("INVALID_CREDENTIALS"));

        authenticationController.setAuthenticationManager(authenticationManager);
        authenticationController.setUserDetailsService(userDetailsService);
        authenticationController.setJwtTokenUtil(jwtTokenUtil);
    }

    @Test
    public void authenticate_Ok() throws Exception {

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new JwtRequest("username@gmail.com", "password")))
        )
                .andExpect(status().isOk());
    }

    @Test
    public void authenticate_InvalidPassword() throws Exception {

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new JwtRequest("username", "invalid")))
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void authenticate_InvalidUsername() throws Exception {

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new JwtRequest("invalid", "password")))
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void register_Ok() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserDto("username", "email@tut.by", "123")))
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/authenticate"));
    }

    @Test
    public void register_InvalidEmail() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserDto("username", "username", "123")))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Invalid email")));
    }

    @Test
    public void register_InvalidPassword() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserDto("username", "username@gmail.com", "13")))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Password must have at least 3 symbols")));
    }

    @Test
    public void register_ExistingUser() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserDto("username", "username@gmail.com", "pass")))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("User with that email already exists")));
    }
}
