package com.conf.conferences;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class LogFilter extends OncePerRequestFilter {

    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 1000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(requestWrapper, responseWrapper);
        logRequest(requestWrapper);
        logResponse(requestWrapper, responseWrapper);
    }

    private void logRequest(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        String body = null;
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, DEFAULT_MAX_PAYLOAD_LENGTH);
                body = new String(buf, 0, length);
            }
            logger.debug(String.format("Incoming request %s %s?%s from client %s, session %s, user %s\n" +
                            "with headers %s\n" +
                            "and body %s",
                    wrapper.getMethod(),
                    wrapper.getRequestURI(),
                    Optional.ofNullable(wrapper.getQueryString()).orElse(""),
                    wrapper.getRemoteAddr(),
                    Optional.ofNullable(wrapper.getSession(false)).map(HttpSession::getId).orElse(null),
                    wrapper.getRemoteUser(),
                    new ServletServerHttpRequest(request).getHeaders(),
                    body));
        }
    }

    private void logResponse(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper) {
        String body = null;
        byte[] buf = responseWrapper.getContentAsByteArray();

        if (buf.length > 0) {
            int length = Math.min(buf.length, DEFAULT_MAX_PAYLOAD_LENGTH);
            try {
                body = new String(buf, 0, length);
                responseWrapper.copyBodyToResponse();
            } catch (IOException e) {
                logger.error(e);
            }
        }

        logger.debug(String.format("Response from request %s %s?%s from client %s, session %s, user %s\n" +
                        "with status code %s and body %s",
                requestWrapper.getMethod(),
                requestWrapper.getRequestURI(),
                Optional.ofNullable(requestWrapper.getQueryString()).orElse(""),
                requestWrapper.getRemoteAddr(),
                Optional.ofNullable(requestWrapper.getSession(false)).map(HttpSession::getId).orElse(null),
                requestWrapper.getRemoteUser(),
                responseWrapper.getStatus(),
                body));
    }
}

