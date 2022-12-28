package com.example.springclient.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;

record PracticalAdvice(@JsonProperty("message") String message,
                       @JsonProperty("identifier") int identifier) {
}