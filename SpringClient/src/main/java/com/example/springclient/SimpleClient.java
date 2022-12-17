package com.example.springclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Companies", url = "http://localhost:8099")
public interface SimpleClient {

    @GetMapping("/companies/{companyId}")
    boolean findById (@PathVariable Integer companyId);

    @GetMapping("/companies/getOne/{companyId}")
    String getOne(@PathVariable Integer companyId);
}