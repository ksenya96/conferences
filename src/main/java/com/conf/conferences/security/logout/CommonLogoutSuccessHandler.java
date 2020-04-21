package com.conf.conferences.security.logout;

import com.conf.conferences.db.UserService;
import com.conf.conferences.security.jwt.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.util.Optional;

@Component
@Slf4j
public class CommonLogoutSuccessHandler implements LogoutSuccessHandler {

    private JwtTokenUtil jwtTokenUtil;

    private UserService userService;

    private InvalidTokenRepository invalidTokenRepository;

    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setInvalidTokenRepository(InvalidTokenRepository invalidTokenRepository) {
        this.invalidTokenRepository = invalidTokenRepository;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String requestTokenHeader = request.getHeader("Authorization");

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);
            try {
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);

                if (username != null) {
                    UserDetails userDetails = Optional.ofNullable(this.userService.loadUserByUsername(username))
                            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

                    if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                        InvalidToken invalidToken = new InvalidToken();
                        invalidToken.setToken(jwtToken);
                        invalidToken.setExpirationTime(jwtTokenUtil.getExpirationDateFromToken(jwtToken)
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime());
                        invalidTokenRepository.saveAndFlush(invalidToken);
                    }
                }
            } catch (IllegalArgumentException e) {
                log.warn("Unable to get JWT Token from request " + request.getMethod() + " " + request.getRequestURI());
            } catch (ExpiredJwtException e) {
                log.warn("JWT Token has expired from request " + request.getMethod() + " " + request.getRequestURI());
            }
        } else {
            log.warn("JWT Token does not begin with Bearer String");
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
