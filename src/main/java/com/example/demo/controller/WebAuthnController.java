package com.example.demo.controller;

import com.webauthn4j.springframework.security.WebAuthnAuthenticationRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/webauthn")
public class WebAuthnController {

    /*private final WebAuthnAuthenticatorService authenticatorService;

    public WebAuthnController(WebAuthnAuthenticatorService authenticatorService) {
        this.authenticatorService = authenticatorService;
    }

    @PostMapping("/register")
    public String register(@RequestBody WebAuthnRegistrationRequest request) {
        // Handle registration request
        return authenticatorService.register(request);
    }

    @PostMapping("/authenticate")
    public String authenticate(@RequestBody WebAuthnAuthenticationRequest request) {
        // Handle authentication request
        return authenticatorService.authenticate(request);
    }*/
}
