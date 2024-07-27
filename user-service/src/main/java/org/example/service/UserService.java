package org.example.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.example.model.UserDTO;
import org.example.repository.UserRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAll() {
        log.info("Searching for all users");
        return userRepository.findAll();
    }

    public User getById(@NotNull Long id) {
        log.info("Searching for user with id={}", id);
        return userRepository.findById(id)
                .orElse(null);
    }

    public List<User> getByIds(Collection<Long> ids) {
        log.info("Searching for users with ids={}", ids);
        return userRepository.findAllByIdIn(ids);
    }

    public User getByUsername(@NotNull String username) {

        log.info("Searching for user with username={}", username);

        var userExample = new User();
        userExample.setUsername(username);

        return userRepository.findOne(Example.of(userExample))
                .orElse(null);
    }

    public User create(@NotNull UserDTO userDTO) {

        log.info("Creating user '{}'", userDTO);

        var user = userDTO.toUser();
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDate.now());
        }

        return userRepository.save(user);
    }

    @Transactional
    public User update(@NotNull Long id, @NotNull UserDTO userDTO) {

        log.info("Updating user with id={}, {}", id, userDTO);

        var userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            log.error("User with id={} not found", id);
            return null;
        }
        var user = userOptional.get();

        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getCreatedAt() != null) {
            user.setCreatedAt(userDTO.getCreatedAt());
        }

        return userRepository.save(user);
    }

    public void deleteById(@NotNull Long id) {
        log.warn("Deleting user id={}", id);
        userRepository.deleteById(id);
    }
}
