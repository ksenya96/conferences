package com.conf.conferences;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorModel> handleSpringExceptions(Exception e) {
        ErrorModel errorModel = new ErrorModel(e.getMessage());
        return ResponseEntity.status(401).body(errorModel);

    }
}
