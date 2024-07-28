package org.example.controller.controllerAdvice;

import org.example.model.users.User;
import org.example.service.users.UserService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthenticationControllerAdviceTest {

    @Autowired
    private AuthenticationControllerAdvice authenticationControllerAdvice;

    @MockBean
    private UserService userService;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnNullIfPrincipalIsNullWhenGetCurrentUser() {

        var userReceived = authenticationControllerAdvice.getCurrentUser(null);
        assertNull(userReceived);

        verify(userService, never()).getByUsername(any());
        verify(userService, never()).create(any());
    }

    @Test
    void shouldReturnNullIfUsernameIsEmptyWhenGetCurrentUser() {

        var principal = new Principal() {
            @Override
            public String getName() {
                return null;
            }
        };

        var userReceived = authenticationControllerAdvice.getCurrentUser(principal);
        assertNull(userReceived);

        verify(userService, never()).getByUsername(any());
        verify(userService, never()).create(any());
    }

    @Test
    void shouldReturnFoundUserWhenGetCurrentUser() {

        var userExisting = easyRandom.nextObject(User.class);
        var username = userExisting.getUsername();

        var principal = new Principal() {
            @Override
            public String getName() {
                return username;
            }
        };

        when(userService.getByUsername(username)).thenReturn(userExisting);

        var userReceived = authenticationControllerAdvice.getCurrentUser(principal);
        assertEquals(userExisting, userReceived);
        assertEquals(userExisting.getId(), userReceived.getId());

        verify(userService, times(1)).getByUsername(username);
        verify(userService, only()).getByUsername(username);
    }

    @Test
    void shouldReturnCreatedUserWhenGetCurrentUser() {

        var username = "test-username";

        var userToBeCreated = new User();
        userToBeCreated.setUsername(username);

        var principal = new Principal() {
            @Override
            public String getName() {
                return username;
            }
        };

        when(userService.create(any(User.class))).thenAnswer(ans -> ans.getArgument(0, User.class));

        var userReceived = authenticationControllerAdvice.getCurrentUser(principal);
        assertEquals(userToBeCreated, userReceived);
        assertEquals(userToBeCreated.getId(), userReceived.getId());

        verify(userService, times(1)).getByUsername(username);
        verify(userService, times(1)).create(userToBeCreated);
    }
}