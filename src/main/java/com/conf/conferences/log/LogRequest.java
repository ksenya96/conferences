package com.conf.conferences.log;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

@Getter
@Builder
public class LogRequest {
    private String method;
    private String uri;
    private String queryParams;
    private String remoteAddr;
    private String session;
    private String remoteUser;
    private HttpHeaders httpHeaders;
    private Object body;
}
