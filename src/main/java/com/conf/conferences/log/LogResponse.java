package com.conf.conferences.log;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class LogResponse {
    private String method;
    private String uri;
    private String queryParams;
    private String remoteAddr;
    private String session;
    private String remoteUser;
    private int status;
    private Object body;
}
