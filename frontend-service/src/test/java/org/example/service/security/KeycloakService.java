package org.example.service.security;

import org.springframework.stereotype.Service;

@Service
public class KeycloakService {

    public String getLoginURL() {
        return "http://test.com/login";
    }

    public String getRegistrationURL() {
        return "http://test.com/register";
    }

    public String getLogoutURL() {
        return "http://test.com/logout";
    }
}
