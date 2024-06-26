package org.example.service.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.NotFoundException;
import org.example.model.users.User;
import org.example.repository.users.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAll() {
        log.info("Searching for all users");
        return userRepository.getAll();
    }

    public User getById(Long id) {
        log.info("Searching for user with id={}", id);
        return userRepository.getById(id);
    }

    public User getByUsername(String username) {
        log.info("Searching for user with username={}", username);
        return userRepository.getByUsername(username);
    }

    public User create(User user) {
        log.info("Creating user '{}'", user);
        return userRepository.create(user);
    }

    public User update(Long id, User user) {
        log.info("Updating user with id={}, {}", id, user);
        var userUpdated = userRepository.update(id, user);
        if (userUpdated == null) {
            throw new NotFoundException(String.format("User with id=%s not found", id));
        }
        return userUpdated;
    }

    public void deleteById(Long id) {
        log.warn("Deleting user id={}", id);
        userRepository.deleteById(id);
    }
}
