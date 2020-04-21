package com.conf.conferences.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("User with that email already exists");
    }
}
