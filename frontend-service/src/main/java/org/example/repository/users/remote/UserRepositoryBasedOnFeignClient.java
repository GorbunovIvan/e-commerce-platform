package org.example.repository.users.remote;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.users.User;
import org.example.repository.users.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Primary
@ConditionalOnProperty(name = "user-service.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryBasedOnFeignClient implements UserRepository {

    private final UserServiceFeignClient userServiceFeignClient;

    @Override
    public List<User> getAll() {
        log.info("Searching for all users");
        var response = userServiceFeignClient.getAll();
        if (response.getStatusCode().isError()) {
            logRemoteServiceError(response);
            return Collections.emptyList();
        }
        return response.getBody();
    }

    @Override
    public User getById(Long id) {
        log.info("Searching for user with id={}", id);
        var response = userServiceFeignClient.getById(id);
        if (response.getStatusCode().isError()) {
            logRemoteServiceError(response);
            return null;
        }
        return response.getBody();
    }

    @Override
    public User getByUsername(String username) {
        log.info("Searching for user with username={}", username);
        var response = userServiceFeignClient.getByUsername(username);
        if (response.getStatusCode().isError()) {
            logRemoteServiceError(response);
            return null;
        }
        return response.getBody();
    }

    @Override
    public User create(User user) {
        log.info("Creating user '{}'", user);
        var response = userServiceFeignClient.create(user);
        if (response.getStatusCode().isError()) {
            logRemoteServiceError(response);
            return null;
        }
        return response.getBody();
    }

    @Override
    public User update(Long id, User user) {
        log.info("Updating user with id={}, {}", id, user);
        var response = userServiceFeignClient.update(id, user);
        if (response.getStatusCode().isError()) {
            logRemoteServiceError(response);
            return null;
        }
        return response.getBody();
    }

    @Override
    public void deleteById(Long id) {
        log.warn("Deleting user id={}", id);
        var response = userServiceFeignClient.deleteById(id);
        if (response.getStatusCode().isError()) {
            logRemoteServiceError(response);
        }
    }

    private void logRemoteServiceError(ResponseEntity<?> response) {
        var errorTitle = "Remote user-service is not available";
        log.error("{}. {} - {}", errorTitle, response.getStatusCode(), response.getBody());
    }
}
