package com.example.springclient;

import com.example.springclient.models.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.text.ParseException;
import java.util.Date;

@SpringBootApplication
@EnableFeignClients
public class SpringClientApplication {

    public static void main(String[] args) throws JsonProcessingException {
        SpringApplication.run(SpringClientApplication.class, args);
    }

    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }
}
