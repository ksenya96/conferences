package com.conf.conferences.security;

import com.conf.conferences.db.SocialType;
import com.conf.conferences.db.User;
import com.conf.conferences.db.UserService;
import com.conf.conferences.security.jwt.JwtResponse;
import com.conf.conferences.security.jwt.JwtTokenUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        SocialType socialType = getResourceName(request);
        UserDetails userDetails = userService.loadUserByUsernameAndOauth2Resource(authentication.getName(), socialType);
        if (userDetails == null) {
            User user = new User();
            user.setUsername(authentication.getName());
            user.setName(authentication.getName());
            user.setOauth2Resource(socialType);
            userService.saveAndFlush(user);
            userDetails = user;
        }

        final String token = jwtTokenUtil.generateToken(userDetails);
        JwtResponse jwtResponse = new JwtResponse(token);
        String jwtResponseBody = new Gson().toJson(jwtResponse);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(jwtResponseBody);
        out.flush();
    }

    private SocialType getResourceName(HttpServletRequest request) throws IOException {
        String uri = request.getRequestURI();
        if (!uri.contains("/login")) {
            throw new IOException("Unknown authorization URI");
        }
        String resourceName = uri.substring(uri.lastIndexOf('/') + 1);
        return SocialType.valueOf(resourceName.toUpperCase());
    }
}
