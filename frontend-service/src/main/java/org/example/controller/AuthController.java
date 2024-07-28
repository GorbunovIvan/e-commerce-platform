package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.users.User;
import org.example.service.security.KeycloakService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final KeycloakService keycloakService;

    @GetMapping("/current-user")
    public String currentUser(@ModelAttribute("currentUser") User currentUser) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        return "redirect:/users/" + currentUser.getId();
    }

    @GetMapping("/login")
    public String login() {
        var loginURL = keycloakService.getLoginURL();
        return "redirect:" + loginURL;
    }

    @GetMapping("/register")
    public String register() {
        var registrationURL = keycloakService.getRegistrationURL();
        return "redirect:" + registrationURL;
    }

    @GetMapping("/after-registration")
    public String afterRegistration() {
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout() {
        var logoutURL = keycloakService.getLogoutURL();
        return "redirect:" + logoutURL;
    }
}
