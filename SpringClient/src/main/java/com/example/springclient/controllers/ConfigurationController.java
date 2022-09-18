package com.example.springclient.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class ConfigurationController {

    @Autowired
    private Environment env;

    @RequestMapping("/configurations")
    public String getConfigurations() {
        return env.getProperty("keycloak.realm");
    }
}