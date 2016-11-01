package com.aces.application.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthenticationController {
	
	@RequestMapping("/login")
    public String login() {
        return "login";
    }
}