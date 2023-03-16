package com.agileavengers.icuconnectbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {
    @GetMapping(value = "/hello")
    public String getEmployees(@RequestParam String param) {
        return "hello world";
    }

}
