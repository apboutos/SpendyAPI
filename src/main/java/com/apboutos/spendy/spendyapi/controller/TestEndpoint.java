package com.apboutos.spendy.spendyapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/test",produces = "application/json")
public class TestEndpoint {


    @GetMapping
    public String test() {
        return "Test endpoint is OK";
    }
}
