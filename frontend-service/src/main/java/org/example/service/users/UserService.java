package org.example.service.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.NotFoundException;
import org.example.model.users.User;
import org.example.repository.users.UserRepository;
import org.example.service.ModelBinder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ModelBinder modelBinder;

    public List<User> getAll() {
        log.info("Searching for all users");
        var result = userRepository.getAll();
        return modelBinder.bindFields(result);
    }

    public User getById(Long id) {
        log.info("Searching for user with id={}", id);
        var result = userRepository.getById(id);
        return modelBinder.bindFields(result);
    }

    public User getByUsername(String username) {
        log.info("Searching for user with username={}", username);
        var result = userRepository.getByUsername(username);
        return modelBinder.bindFields(result);
    }

    public User create(User user) {
        log.info("Creating user '{}'", user);
        var result = userRepository.create(user);
        return modelBinder.bindFields(result);
    }

    public User update(Long id, User user) {
        log.info("Updating user with id={}, {}", id, user);
        var userUpdated = userRepository.update(id, user);
        if (userUpdated == null) {
            throw new NotFoundException(String.format("User with id=%s not found", id));
        }
        return modelBinder.bindFields(userUpdated);
    }

    public void deleteById(Long id) {
        log.warn("Deleting user id={}", id);
        userRepository.deleteById(id);
    }
}
