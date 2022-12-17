package com.example.springclient;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AppController {

    private final SimpleClient client;

    @GetMapping("/companies/{companyId}")
    public boolean findById(@PathVariable Integer companyId) {
        return client.findById(companyId);
    }

    @GetMapping("/companies/getOne/{companyId}")
    public String getOne(@PathVariable Integer companyId) {
        return client.getOne(companyId);
    }

}