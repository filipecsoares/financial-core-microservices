package com.fcs.accounts.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @GetMapping("/test")
    public String test() {
        return "Hello from AccountController!";
    }
}
