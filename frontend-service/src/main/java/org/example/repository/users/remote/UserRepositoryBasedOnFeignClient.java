package org.example.repository.users.remote;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.users.User;
import org.example.repository.FeignClientBaseClass;
import org.example.repository.users.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@Primary
@ConditionalOnProperty(name = "user-service.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryBasedOnFeignClient extends FeignClientBaseClass implements UserRepository {

    private final UserServiceFeignClient userServiceFeignClient;

    @Override
    protected String getServiceName() {
        return "user-service";
    }

    @Override
    public List<User> getAll() {
        log.info("Searching for all users");
        return makeARequest(userServiceFeignClient::getAll, Collections::emptyList);
    }

    @Override
    public User getById(Long id) {
        log.info("Searching for user with id={}", id);
        return makeARequest(() -> userServiceFeignClient.getById(id));
    }

    @Override
    public List<User> getByIds(Set<Long> ids) {
        log.info("Searching for users with ids={}", ids);
        return makeARequest(() -> userServiceFeignClient.getByIds(ids), Collections::emptyList);
    }

    @Override
    public User getByUsername(String username) {
        log.info("Searching for user with username={}", username);
        return makeARequest(() -> userServiceFeignClient.getByUsername(username));
    }

    @Override
    public User create(User user) {
        log.info("Creating user '{}'", user);
        return makeARequest(() -> userServiceFeignClient.create(user));
    }

    @Override
    public User update(Long id, User user) {
        log.info("Updating user with id={}, {}", id, user);
        return makeARequest(() -> userServiceFeignClient.update(id, user));
    }

    @Override
    public void deleteById(Long id) {
        log.warn("Deleting user id={}", id);
        makeARequest(() -> userServiceFeignClient.deleteById(id));
    }
}
