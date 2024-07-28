package org.example.repository.users;

import org.example.model.users.User;
import org.example.repository.ReflectUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryDummyTest {

    @Autowired
    private UserRepositoryDummy userRepositoryDummy;

    @Autowired
    private ReflectUtil reflectUtil;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        setUsersToUserRepositoryDummy(new ArrayList<>());
    }

    @Test
    void shouldReturnListOfUsersWhenGetAll() {

        var usersExpected = easyRandom.objects(User.class, 3).toList();
        setUsersToUserRepositoryDummy(new ArrayList<>(usersExpected));

        var users = userRepositoryDummy.getAll();
        assertNotNull(users);
        assertEquals(getUsersFromUserRepositoryDummy(), users);
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {
        var users = userRepositoryDummy.getAll();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void shouldReturnUserWhenGetById() {

        var userExpected = easyRandom.nextObject(User.class);
        setUsersToUserRepositoryDummy(new ArrayList<>(List.of(userExpected)));

        var id = userExpected.getId();

        var user = userRepositoryDummy.getById(id);
        assertNotNull(user);
        assertEquals(userExpected, user);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var userExpected = easyRandom.nextObject(User.class);
        setUsersToUserRepositoryDummy(new ArrayList<>(List.of(userExpected)));

        var id = 999L;
        var user = userRepositoryDummy.getById(id);

        assertNull(user);
    }

    @Test
    void shouldReturnListOfUsersWhenGetByIds() {

        var usersExpected = easyRandom.objects(User.class, 5).toList();
        setUsersToUserRepositoryDummy(new ArrayList<>(usersExpected));

        var ids = usersExpected.stream().map(User::getId).collect(Collectors.toSet());

        var users = userRepositoryDummy.getByIds(ids);
        assertNotNull(users);
        assertEquals(getUsersFromUserRepositoryDummy(), users);
    }

    @Test
    void shouldReturnEmptyListOfUsersWhenGetByIds() {

        var usersExpected = easyRandom.objects(User.class, 5).toList();
        setUsersToUserRepositoryDummy(new ArrayList<>(usersExpected));

        var ids = easyRandom.objects(Long.class, 3).collect(Collectors.toSet());

        var users = userRepositoryDummy.getByIds(ids);
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void shouldReturnUserWhenGetByUsername() {

        var userExpected = easyRandom.nextObject(User.class);
        setUsersToUserRepositoryDummy(new ArrayList<>(List.of(userExpected)));

        var username = userExpected.getUsername();

        var user = userRepositoryDummy.getByUsername(username);
        assertNotNull(user);
        assertEquals(userExpected, user);
    }

    @Test
    void shouldReturnNullWhenGetByUsername() {

        var userExpected = easyRandom.nextObject(User.class);
        setUsersToUserRepositoryDummy(new ArrayList<>(List.of(userExpected)));

        var username = "username-of-user";
        var user = userRepositoryDummy.getByUsername(username);
        assertNull(user);
    }

    @Test
    void shouldCreateAndReturnNewUserWhenCreate() {

        var usersExisting = easyRandom.objects(User.class, 5).toList();
        setUsersToUserRepositoryDummy(new ArrayList<>(usersExisting));

        var userExpected = easyRandom.nextObject(User.class);

        var user = userRepositoryDummy.create(userExpected);
        assertNotNull(user);
        assertEquals(userExpected, user);

        var usersAfterOperation = getUsersFromUserRepositoryDummy();
        assertTrue(usersAfterOperation.contains(user));
        assertEquals(usersExisting.size() + 1, usersAfterOperation.size());
    }

    @Test
    void shouldUpdateAndReturnUserWhenUpdate() {

        var usersExisting = easyRandom.objects(User.class, 5).toList();
        setUsersToUserRepositoryDummy(new ArrayList<>(usersExisting));

        var userExisting = usersExisting.getFirst();
        var id = userExisting.getId();

        var userUpdated = userRepositoryDummy.update(id, userExisting);
        assertNotNull(userUpdated);
        assertEquals(id, userUpdated.getId());
        assertEquals(userExisting, userUpdated);

        var usersAfterOperation = getUsersFromUserRepositoryDummy();
        assertTrue(usersAfterOperation.contains(userUpdated));
        assertEquals(usersExisting.size(), usersAfterOperation.size());
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var usersExisting = easyRandom.objects(User.class, 5).toList();
        setUsersToUserRepositoryDummy(new ArrayList<>(usersExisting));

        var id = 1L;

        var userReceived = userRepositoryDummy.update(id, new User());
        assertNull(userReceived);
    }

    @Test
    void shouldDeleteUserWhenDelete() {

        var usersExising = easyRandom.objects(User.class, 3).toList();
        setUsersToUserRepositoryDummy(new ArrayList<>(usersExising));

        var userToDelete = usersExising.getFirst();
        var id = userToDelete.getId();

        userRepositoryDummy.deleteById(id);

        var usersAfterOperation = getUsersFromUserRepositoryDummy();
        assertFalse(usersAfterOperation.contains(userToDelete));
        assertEquals(usersExising.size() - 1, usersAfterOperation.size());
    }

    @Test
    void shouldNotDeleteUserWhenDelete() {

        var usersExising = easyRandom.objects(User.class, 3).toList();
        setUsersToUserRepositoryDummy(new ArrayList<>(usersExising));

        var id = 77L;

        userRepositoryDummy.deleteById(id);

        var usersAfterOperation = getUsersFromUserRepositoryDummy();
        assertEquals(usersExising.size(), usersAfterOperation.size());
    }

    private List<User> getUsersFromUserRepositoryDummy() {
        return reflectUtil.getValueOfObjectField(userRepositoryDummy, "users", Collections::emptyList);
    }

    private void setUsersToUserRepositoryDummy(List<User> users) {
        reflectUtil.setValueToObjectField(userRepositoryDummy, "users", users);
    }
}