package org.example.service;

import org.example.model.User;
import org.example.model.UserDTO;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldReturnListOfUsersWhenGetAll() {

        var usersExpected = List.of(
                new User(1L, "user-1", LocalDate.now().minusDays(3)),
                new User(3L, "user-2", LocalDate.now().minusDays(2)),
                new User(3L, "user-3", LocalDate.now().minusDays(1))
        );

        when(userRepository.findAll()).thenReturn(usersExpected);

        var users = userService.getAll();
        assertNotNull(users);
        assertEquals(usersExpected, users);

        verify(userRepository, times(1)).findAll();
        verify(userRepository, only()).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {

        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        var users = userService.getAll();
        assertNotNull(users);
        assertTrue(users.isEmpty());

        verify(userRepository, times(1)).findAll();
        verify(userRepository, only()).findAll();
    }

    @Test
    void shouldReturnUserWhenGetById() {

        var id = 1L;
        var userExpected = new User(id, "user-by-id", LocalDate.now().minusMonths(5));

        when(userRepository.findById(id)).thenReturn(Optional.of(userExpected));

        var user = userService.getById(id);
        assertNotNull(user);
        assertEquals(userExpected, user);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, only()).findById(1L);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = 1L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        var user = userService.getById(id);
        assertNull(user);

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, only()).findById(id);
    }

    @Test
    void shouldReturnUserWhenGetByUsername() {

        var username = "username-of-user";
        var userExpected = new User(99L, username, LocalDate.now().minusWeeks(7));

        when(userRepository.findOne(any())).thenReturn(Optional.of(userExpected));

        var user = userService.getByUsername(username);
        assertNotNull(user);
        assertEquals(userExpected, user);

        verify(userRepository, times(1)).findOne(any());
        verify(userRepository, only()).findOne(any());
    }

    @Test
    void shouldReturnNullWhenGetByUsername() {

        var username = "username-of-user";

        when(userRepository.findOne(any())).thenReturn(Optional.empty());

        var user = userService.getByUsername(username);
        assertNull(user);

        verify(userRepository, times(1)).findOne(any());
        verify(userRepository, only()).findOne(any());
    }

    @Test
    void shouldCreateAndReturnNewUserWhenCreate() {

        when(userRepository.save(any(User.class))).thenAnswer(answer -> {
            User user = answer.getArgument(0);
            user.setId(99L);
            return user;
        });

        var userDTO = new UserDTO("new-user");

        var user = userService.create(userDTO);
        assertNotNull(user);
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
        assertEquals(userDTO.getUsername(), user.getUsername());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, only()).save(any(User.class));
    }

    @Test
    void shouldUpdateAndReturnUserWhenUpdate() {

        var id = 1L;
        var userExisting = new User(id, "user-1", LocalDate.now());

        when(userRepository.findById(id)).thenReturn(Optional.of(userExisting));
        when(userRepository.save(any(User.class))).thenAnswer(answer -> answer.getArgument(0));

        var userDTO = new UserDTO("new-username", LocalDate.now().minusDays(10));

        var userUpdated = userService.update(id, userDTO);
        assertNotNull(userUpdated);
        assertEquals(id, userUpdated.getId());
        assertEquals(userDTO.getUsername(), userUpdated.getUsername());
        assertEquals(userDTO.getCreatedAt(), userUpdated.getCreatedAt());

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        var userDTO = new UserDTO("new-username", LocalDate.now().minusDays(10));

        var userUpdated = userService.update(id, userDTO);
        assertNull(userUpdated);

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldDeleteUserWhenDelete() {

        var id = 1L;

        userService.deleteById(id);

        verify(userRepository, times(1)).deleteById(id);
        verify(userRepository, only()).deleteById(id);
    }
}