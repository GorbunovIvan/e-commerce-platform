package org.example.controller.converter;

import org.example.model.users.User;
import org.example.repository.users.UserRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserConverterTest {

    @Autowired
    private UserConverter userConverter;

    @MockBean
    private UserRepository userRepository;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    public void shouldReturnNullWhenConvert() {
        var userReceived = userConverter.convert("");
        assertNull(userReceived);
    }

    @Test
    public void shouldReturnUserFromIdWhenConvert() {

        var user = easyRandom.nextObject(User.class);
        user.setId(7654L);

        var id = user.getId();

        when(userRepository.getById(id)).thenReturn(user);

        var userReceived = userConverter.convert(String.valueOf(id));
        assertNotNull(userReceived);
        assertEquals(user, userReceived);

        verify(userRepository, times(1)).getById(id);
    }

    @Test
    public void shouldReturnNullFromIdWhenConvert() {

        var id = 35L;

        var userReceived = userConverter.convert(String.valueOf(id));
        assertNull(userReceived);

        verify(userRepository, times(1)).getById(id);
    }

    @Test
    public void shouldReturnUserFromUniqueViewWhenConvert() {

        var user = easyRandom.nextObject(User.class);
        user.setId(4567456L);

        var userString = user.getUniqueView();

        when(userRepository.getById(user.getId())).thenReturn(user);

        var userReceived = userConverter.convert(userString);
        assertNotNull(userReceived);
        assertEquals(user, userReceived);

        verify(userRepository, times(1)).getById(user.getId());
    }

    @Test
    public void shouldReturnUserFromUsernameWhenConvert() {

        var user = easyRandom.nextObject(User.class);
        var username = user.getUsername();

        when(userRepository.getByUsername(username)).thenReturn(user);

        var userReceived = userConverter.convert(username);
        assertNotNull(userReceived);
        assertEquals(user, userReceived);

        verify(userRepository, times(1)).getByUsername(username);
    }

    @Test
    public void shouldReturnNewUserWhenConvert() {

        var sourceString = "test user";

        var userReceived = userConverter.convert(sourceString);
        assertNotNull(userReceived);
        assertNull(userReceived.getId());
        assertEquals(sourceString, userReceived.getUsername());
    }
}