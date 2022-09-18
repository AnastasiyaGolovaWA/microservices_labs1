package com.example.springclient.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class HelloController {

    @Autowired
    private Environment env;

    @RequestMapping("/hello")
    public String getUserPath() {
        return env.getProperty("text.greeting");
    }
}