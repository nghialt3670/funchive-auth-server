package com.funchive.authserver.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/auth")
public class AuthController {

    @PostMapping("/register")
    public void register() {

    }

}
