package com.conf.conferences.db;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
public class UserDto {
    @ApiModelProperty(value = "Username")
    @NotBlank(message = "username cannot be empty")
    private String username;

    @ApiModelProperty(value = "Email")
    @Email(message = "Invalid email")
    private String email;

    @ApiModelProperty(value = "Password. Must have at least 3 symbols")
    @Size(min = 3, message = "Password must have at least 3 symbols")
    private String password;
}
