package com.conf.conferences.controllers;

import com.conf.conferences.ErrorModel;
import com.conf.conferences.db.User;
import com.conf.conferences.db.UserDto;
import com.conf.conferences.db.UserService;
import com.conf.conferences.exceptions.UserAlreadyExistsException;
import com.conf.conferences.security.jwt.JwtRequest;
import com.conf.conferences.security.jwt.JwtResponse;
import com.conf.conferences.security.jwt.JwtTokenUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Optional;

import static com.conf.conferences.ApiConstants.BAD_REQUEST_CODE;
import static com.conf.conferences.ApiConstants.BAD_REQUEST_MESSAGE;
import static com.conf.conferences.ApiConstants.CREATED_CODE;
import static com.conf.conferences.ApiConstants.CREATED_MESSAGE;
import static com.conf.conferences.ApiConstants.INTERNAL_SERVER_ERROR_CODE;
import static com.conf.conferences.ApiConstants.INTERNAL_SERVER_ERROR_MESSAGE;
import static com.conf.conferences.ApiConstants.OK_CODE;
import static com.conf.conferences.ApiConstants.OK_MESSAGE;
import static com.conf.conferences.ApiConstants.UNAUTHORIZED_CODE;
import static com.conf.conferences.ApiConstants.UNAUTHORIZED_MESSAGE;


@RestController(value = "AuthenticationController")
@CrossOrigin
public class AuthenticationController {

    private AuthenticationManager authenticationManager;

    private JwtTokenUtil jwtTokenUtil;

    private UserService userDetailsService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Autowired
    public void setUserDetailsService(UserService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    //Для OAuth2 /login/google
    @ApiOperation(value = "Authentication with login and password",
            response = JwtResponse.class,
            notes = "without authorities")
    @ApiResponses(value = {
            @ApiResponse(code = OK_CODE, message = OK_MESSAGE, response = JwtResponse.class),
            @ApiResponse(code = BAD_REQUEST_CODE, message = BAD_REQUEST_MESSAGE, response = ErrorModel.class),
            @ApiResponse(code = UNAUTHORIZED_CODE, message = UNAUTHORIZED_MESSAGE, response = ErrorModel.class),
            @ApiResponse(code = INTERNAL_SERVER_ERROR_CODE, message = INTERNAL_SERVER_ERROR_MESSAGE, response = ErrorModel.class)
    })
    @PostMapping(value = "/authenticate")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {

        final UserDetails userDetails = Optional.ofNullable(
                userDetailsService.loadUserByUsername(authenticationRequest.getEmail())
        ).orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + authenticationRequest.getEmail()));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(), authenticationRequest.getPassword()));

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));

    }

    @ApiOperation(value = "User registration",
            notes = "without authorities")
    @ApiResponses(value = {
            @ApiResponse(code = CREATED_CODE, message = CREATED_MESSAGE),
            @ApiResponse(code = BAD_REQUEST_CODE, message = BAD_REQUEST_MESSAGE, response = ErrorModel.class),
            @ApiResponse(code = UNAUTHORIZED_CODE, message = UNAUTHORIZED_MESSAGE, response = ErrorModel.class),
            @ApiResponse(code = INTERNAL_SERVER_ERROR_CODE, message = INTERNAL_SERVER_ERROR_MESSAGE, response = ErrorModel.class)
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) {
        if (userDetailsService.loadUserByUsername(userDto.getEmail()) != null) {
            throw new UserAlreadyExistsException();
        }
        User user = new User();
        user.setUsername(userDto.getEmail());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEnabled(true);
        userDetailsService.saveAndFlush(user);

        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequestUri()
                        .replacePath("/authenticate")
                        .build().toUri())
                .build();
    }
}