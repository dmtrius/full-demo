package com.example.demo.controller;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/health")
    @RolesAllowed("ROLE_USER")
    public String health() {
        return "hello";
    }
}
