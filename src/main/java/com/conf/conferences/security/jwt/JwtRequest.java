package com.conf.conferences.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class JwtRequest implements Serializable {

    private static final long serialVersionUID = -770335567709653456L;

    private String username;
    private String password;
}
