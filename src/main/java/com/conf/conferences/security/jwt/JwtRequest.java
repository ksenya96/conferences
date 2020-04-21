package com.conf.conferences.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class JwtRequest implements Serializable {

    private static final long serialVersionUID = -770335567709653456L;

    @NotBlank(message = "Login cannot be empty")
    private String email;
    @NotBlank(message = "Password cannot be empty")
    private String password;
}
