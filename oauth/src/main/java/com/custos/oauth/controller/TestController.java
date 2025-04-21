package com.custos.oauth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test/auth")
    public String testAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Authenticated as: " + auth.getName() + 
               "\nAuthorities: " + auth.getAuthorities();
    }
} 