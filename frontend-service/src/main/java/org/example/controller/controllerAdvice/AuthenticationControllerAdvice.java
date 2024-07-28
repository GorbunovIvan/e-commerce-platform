package org.example.controller.controllerAdvice;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.users.User;
import org.example.service.users.UserService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Objects;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class AuthenticationControllerAdvice {

    private final UserService userService;

    @ModelAttribute("currentUser")
    public User getCurrentUser(Principal principal) {

        if (principal == null) {
            return null;
        }
        var username = principal.getName();
        if (Objects.requireNonNullElse(username, "").isEmpty()) {
            log.error("Principal '{}' cannot be defined", principal);
            return null;
        }

        var user = userService.getByUsername(username);
        if (user != null) {
            return user;
        }

        log.warn("Principal with username '{}' not found, trying to create new user. (Principal: {}", username, principal);
        return createUserFromPrincipal(principal);
    }

    private User createUserFromPrincipal(@NotNull Principal principal) {
        var user = new User();
        user.setUsername(principal.getName());
        return userService.create(user);
    }
}
