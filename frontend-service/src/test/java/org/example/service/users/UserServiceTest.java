package org.example.service.users;

import org.example.exception.NotFoundException;
import org.example.model.users.User;
import org.example.repository.users.UserRepository;
import org.example.service.modelsBinding.ModelBinder;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ModelBinder modelBinder;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        when(modelBinder.bindFields(any())).thenAnswer(ans -> ans.getArgument(0));
    }

    @Test
    void shouldReturnListOfUsersWhenGetAll() {

        var usersExpected = easyRandom.objects(User.class, 3).toList();

        when(userRepository.getAll()).thenReturn(usersExpected);

        var users = userService.getAll();
        assertNotNull(users);
        assertEquals(usersExpected, users);

        verify(userRepository, times(1)).getAll();
        verify(modelBinder, times(1)).bindFields(usersExpected);
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {

        when(userRepository.getAll()).thenReturn(Collections.emptyList());

        var users = userService.getAll();
        assertNotNull(users);
        assertTrue(users.isEmpty());

        verify(userRepository, times(1)).getAll();
        verify(modelBinder, times(1)).bindFields(Collections.emptyList());
    }

    @Test
    void shouldReturnUserWhenGetById() {

        var userExpected = easyRandom.nextObject(User.class);
        var id = userExpected.getId();

        when(userRepository.getById(id)).thenReturn(userExpected);

        var user = userService.getById(id);
        assertNotNull(user);
        assertEquals(userExpected, user);

        verify(userRepository, times(1)).getById(id);
        verify(modelBinder, times(1)).bindFields(userExpected);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = 1L;

        when(userRepository.getById(id)).thenReturn(null);

        var user = userService.getById(id);
        assertNull(user);

        verify(userRepository, times(1)).getById(id);
        verify(modelBinder, times(1)).bindFields(null);
    }

    @Test
    void shouldReturnUserWhenGetByUsername() {

        var userExpected = easyRandom.nextObject(User.class);
        var username = userExpected.getUsername();

        when(userRepository.getByUsername(username)).thenReturn(userExpected);

        var user = userService.getByUsername(username);
        assertNotNull(user);
        assertEquals(userExpected, user);

        verify(userRepository, times(1)).getByUsername(username);
        verify(modelBinder, times(1)).bindFields(userExpected);
    }

    @Test
    void shouldReturnNullWhenGetByUsername() {

        var username = "username-of-user";

        when(userRepository.getByUsername(username)).thenReturn(null);

        var user = userService.getByUsername(username);
        assertNull(user);

        verify(userRepository, times(1)).getByUsername(username);
        verify(modelBinder, times(1)).bindFields(null);
    }

    @Test
    void shouldCreateAndReturnNewUserWhenCreate() {

        var userExpected = easyRandom.nextObject(User.class);

        when(userRepository.create(userExpected)).thenReturn(userExpected);

        var user = userService.create(userExpected);
        assertNotNull(user);
        assertEquals(userExpected, user);

        verify(userRepository, times(1)).create(userExpected);
        verify(modelBinder, times(1)).bindFields(userExpected);
    }

    @Test
    void shouldUpdateAndReturnUserWhenUpdate() {

        var userExisting = easyRandom.nextObject(User.class);
        var id = userExisting.getId();

        when(userRepository.update(id, userExisting)).thenReturn(userExisting);

        var userUpdated = userService.update(id, userExisting);
        assertNotNull(userUpdated);
        assertEquals(id, userUpdated.getId());
        assertEquals(userExisting, userUpdated);

        verify(userRepository, times(1)).update(id, userExisting);
        verify(modelBinder, times(1)).bindFields(userExisting);
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var id = 1L;

        var user = easyRandom.nextObject(User.class);

        when(userRepository.update(id, user)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.update(id, user));

        verify(userRepository, times(1)).update(id, user);
        verify(modelBinder, never()).bindFields(any());
    }

    @Test
    void shouldDeleteUserWhenDelete() {
        var id = 1L;
        userService.deleteById(id);
        verify(userRepository, times(1)).deleteById(id);
    }
}