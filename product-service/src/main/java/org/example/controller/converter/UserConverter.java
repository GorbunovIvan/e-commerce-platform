package org.example.controller.converter;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class UserConverter implements Converter<String, User> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User convert(@Nullable String source) {

        if (source == null || source.isBlank()) {
            return null;
        }

        var id = stringToLong(source);
        if (id == null) {
            var pattern = Pattern.compile(".*id=(\\d+)");
            id = readIdFromStringByPattern(pattern, source);
        }

        log.info("Converting user by id '{}' to object", id);

        var user = entityManager.find(User.class, id);
        if (user != null) {
            log.info("User with id '{}' exits - {}", id, user);
            return user;
        }

//        log.info("Saving new user with id '{}'", id);
        user = new User(id);
//        entityManager.persist(user);
        return user;
    }

    protected Long readIdFromStringByPattern(Pattern pattern, String str) {
        log.info("Parsing id from string '{}' by pattern: {}", str, pattern);
        try {
            var matcher = pattern.matcher(str);
            if (matcher.find()
                    && matcher.groupCount() == 1) {
                String id = matcher.group(1);
                log.info("Parsed '{}' from string '{}' by pattern: {}", id, str, pattern);
                return stringToLong(id);
            }
        } catch (Exception ignored) {}

        return null;
    }

    protected Long stringToLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception ignored) {}

        return null;
    }
}
