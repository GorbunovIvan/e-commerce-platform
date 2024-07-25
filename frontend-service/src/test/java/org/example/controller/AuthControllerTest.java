package org.example.controller;

import org.example.model.users.User;
import org.example.service.security.KeycloakService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KeycloakService keycloakService;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnRedirectionToUserPageWhenCurrentUser() throws Exception {

        var user = easyRandom.nextObject(User.class);

        mockMvc.perform(get("/auth/current-user")
                        .flashAttr("currentUser", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/" + user.getId()));
    }

    @Test
    void shouldReturnRedirectionToLoginPageWhenCurrentUser() throws Exception {
        mockMvc.perform(get("/auth/current-user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void shouldReturnRedirectionToLoginPageWhenLogin() throws Exception {

        var loginURL = "http://loginURL";

        when(keycloakService.getLoginURL()).thenReturn(loginURL);

        mockMvc.perform(get("/auth/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:" + loginURL));

        verify(keycloakService, times(1)).getLoginURL();
    }

    @Test
    void shouldReturnRedirectionToRegisterPageWhenRegister() throws Exception {

        var registrationURL = "http://registrationURL";

        when(keycloakService.getRegistrationURL()).thenReturn(registrationURL);

        mockMvc.perform(get("/auth/register"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:" + registrationURL));

        verify(keycloakService, times(1)).getRegistrationURL();
    }

    @Test
    void shouldReturnRedirectionToLoginPageWhenAfterRegistration() throws Exception {
        mockMvc.perform(get("/auth/after-registration"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void shouldReturnRedirectionToLogoutPageWhenLogout() throws Exception {

        var logoutURL = "http://registrationURL";

        when(keycloakService.getLogoutURL()).thenReturn(logoutURL);

        mockMvc.perform(get("/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:" + logoutURL));

        verify(keycloakService, times(1)).getLogoutURL();
    }
}