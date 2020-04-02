package com.conf.conferences.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogFilter extends OncePerRequestFilter {

    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 1000;

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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

            LogRequest logRequest = LogRequest.builder()
                    .method(wrapper.getMethod())
                    .uri(wrapper.getRequestURI())
                    .queryParams(wrapper.getQueryString())
                    .remoteAddr(wrapper.getRemoteAddr())
                    .session(Optional.ofNullable(wrapper.getSession(false)).map(HttpSession::getId).orElse(null))
                    .remoteUser(wrapper.getRemoteUser())
                    .httpHeaders(new ServletServerHttpRequest(request).getHeaders())
                    .body(fromJsonToObject(body))
                    .build();

            String jsonLogRequest = fromObjectToJson(logRequest);
            logger.debug("INCOMING REQUEST:\n" + jsonLogRequest);
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

        LogResponse logResponse = LogResponse.builder()
                .method(requestWrapper.getMethod())
                .uri(requestWrapper.getRequestURI())
                .queryParams(requestWrapper.getQueryString())
                .remoteAddr(requestWrapper.getRemoteAddr())
                .session(Optional.ofNullable(requestWrapper.getSession(false)).map(HttpSession::getId).orElse(null))
                .remoteUser(requestWrapper.getRemoteUser())
                .status(responseWrapper.getStatus())
                .body(fromJsonToObject(body))
                .build();
        String logResponseJson = fromObjectToJson(logResponse);
        logger.debug("RESPONSE FROM REQUEST:\n" + logResponseJson);
    }

    private String fromObjectToJson(Object object) {
        String jsonLogRequest = "";
        try {
            jsonLogRequest = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warn("Cannot write object as JSON" + e);
        }
        return jsonLogRequest;
    }

    private Object fromJsonToObject(String json) {
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            log.warn("Cannot read JSON to object" + e);
            return null;
        }
    }
}

