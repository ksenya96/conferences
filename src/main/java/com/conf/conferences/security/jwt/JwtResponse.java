package com.conf.conferences.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = 3032216509566470689L;

    private final String jwtToken;

}
