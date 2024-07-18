package org.example.repository.users;

import lombok.extern.slf4j.Slf4j;
import org.example.model.users.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class UserRepositoryDummy implements UserRepository {

    private final List<User> users = new ArrayList<>();

    @Override
    public List<User> getAll() {
        log.info("Searching for all users");
        return new ArrayList<>(users);
    }

    @Override
    public User getById(Long id) {
        log.info("Searching for user with id={}", id);
        return users.stream()
                .filter(user -> Objects.equals(user.getId(), id))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<User> getByIds(Set<Long> ids) {
        log.info("Searching for users with ids={}", ids);
        return users.stream()
                .filter(user -> ids.contains(user.getId()))
                .toList();
    }

    @Override
    public User getByUsername(String username) {
        log.info("Searching for user with username={}", username);
        return users.stream()
                .filter(user -> Objects.equals(user.getUsername(), username))
                .findAny()
                .orElse(null);
    }

    @Override
    public User create(User user) {
        log.info("Creating user '{}'", user);
        var nextId = nextId();
        user.setId(nextId);
        users.add(user);
        return user;
    }

    @Override
    public synchronized User update(Long id, User user) {
        log.info("Updating user with id={}, {}", id, user);
        var userExisting = getById(id);
        if (userExisting == null) {
            log.error("User with id {} not found", id);
            return null;
        }
        if (user.getUsername() != null) {
            userExisting.setUsername(user.getUsername());
        }
        return userExisting;
    }

    @Override
    public synchronized void deleteById(Long id) {
        log.warn("Deleting user id={}", id);
        var indexOfUserInList = getIndexOfUserInListById(id);
        if (indexOfUserInList == -1) {
            log.error("User with id {} not found in attempt to delete user", id);
            return;
        }
        users.remove(indexOfUserInList);
    }

    private Long nextId() {
        return users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0) + 1;
    }

    private int getIndexOfUserInListById(Long id) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}
