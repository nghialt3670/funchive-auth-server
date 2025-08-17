package com.funchive.authserver.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/oauth2/authorize")
public class AuthorizationServerController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

}
