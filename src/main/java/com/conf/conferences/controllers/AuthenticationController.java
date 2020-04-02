package com.conf.conferences.controllers;

import com.conf.conferences.ErrorModel;
import com.conf.conferences.security.jwt.JwtRequest;
import com.conf.conferences.security.jwt.JwtResponse;
import com.conf.conferences.security.jwt.JwtTokenUtil;
import com.conf.conferences.db.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.conf.conferences.ApiConstants.BAD_REQUEST_CODE;
import static com.conf.conferences.ApiConstants.BAD_REQUEST_MESSAGE;
import static com.conf.conferences.ApiConstants.INTERNAL_SERVER_ERROR_CODE;
import static com.conf.conferences.ApiConstants.INTERNAL_SERVER_ERROR_MESSAGE;
import static com.conf.conferences.ApiConstants.OK_CODE;
import static com.conf.conferences.ApiConstants.OK_MESSAGE;


@RestController(value = "AuthenticationController")
@CrossOrigin
public class AuthenticationController {

    private AuthenticationManager authenticationManager;

    private JwtTokenUtil jwtTokenUtil;

    private UserService userDetailsService;

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

    //Для OAuth2 /login/google
    @ApiOperation(value = "Authentication with login and password",
            response = JwtResponse.class,
            notes = "without authorities")
    @ApiResponses(value = {
            @ApiResponse(code = OK_CODE, message = OK_MESSAGE, response = JwtResponse.class),
            @ApiResponse(code = BAD_REQUEST_CODE, message = BAD_REQUEST_MESSAGE, response = ErrorModel.class),
            @ApiResponse(code = INTERNAL_SERVER_ERROR_CODE, message = INTERNAL_SERVER_ERROR_MESSAGE, response = ErrorModel.class)
    })
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {

        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getUsername());

            final String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}