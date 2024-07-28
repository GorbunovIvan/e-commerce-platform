package org.example.controller.converter;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.users.User;
import org.example.repository.users.UserRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserConverter extends BasicConverter implements Converter<String, User> {

    private final UserRepository userRepository;

    @Override
    public User convert(@Nullable String source) {

        if (source == null || source.isEmpty()) {
            return null;
        }

        log.info("Converting user '{}' to object", source);

        var pattern = User.patternToReadIdFromUniqueView();
        var id = readIdFromStringByPattern(pattern, source);
        if (id == null) {
            id = stringToLong(source);
        }
        if (id != null) {
            var userFound = userRepository.getById(id);
            if (userFound != null) {
                log.info("User with id '{}' exits - {}", id, userFound);
                return userFound;
            } else {
                log.error("User with id '{}' not found", id);
                return null;
            }
        }

        log.info("Converting user '{}' to object by username", source);
        var userFound = userRepository.getByUsername(source);
        if (userFound != null) {
            log.info("User with name '{}' exits - {}", source, userFound);
            return userFound;
        }

        var user = new User();
        user.setUsername(source);
        return user;
    }
}
