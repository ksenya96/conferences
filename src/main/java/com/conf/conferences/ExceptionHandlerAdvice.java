package com.conf.conferences;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorModel> handleSpringExceptions(MethodArgumentNotValidException e) {
        ErrorModel errorModel = new ErrorModel(e.getBindingResult().getFieldError().getDefaultMessage());
        return ResponseEntity.status(400).body(errorModel);

    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<ErrorModel> handleSpringExceptions(UsernameNotFoundException e) {
        ErrorModel errorModel = new ErrorModel(e.getMessage());
        return ResponseEntity.status(401).body(errorModel);

    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorModel> handleSpringExceptions(Exception e) {
        ErrorModel errorModel = new ErrorModel(e.getMessage());
        return ResponseEntity.status(400).body(errorModel);

    }
}
