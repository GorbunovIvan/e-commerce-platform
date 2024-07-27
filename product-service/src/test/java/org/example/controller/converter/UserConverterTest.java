package org.example.controller.converter;

import org.example.model.User;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserConverterTest {

    @Autowired
    private UserConverter userConverter;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnUserWhenConvert() {

        var userId = 98L;
        var user = easyRandom.nextObject(User.class);
        user.setId(userId);

        var userFound = userConverter.convert(Long.toString(userId));
        assertNotNull(userFound);
        assertNotNull(userFound.getId());
        assertEquals(user.getId(), userFound.getId());
        assertEquals(user, userFound);
    }

    @Test
    void shouldReturnNullWhenConvert() {
        var userFound = userConverter.convert(null);
        assertNull(userFound);
    }

    @Test
    void shouldReturnNullFromEmptyStringWhenConvert() {
        var userFound = userConverter.convert("");
        assertNull(userFound);
    }
}