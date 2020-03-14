package com.conf.conferences.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping(value = "/")
    public String hello() {
        return "Hello World";
    }

    @GetMapping(value = "/hello")
    public String sayHello() {
        return "Hello Fax";
    }
}
