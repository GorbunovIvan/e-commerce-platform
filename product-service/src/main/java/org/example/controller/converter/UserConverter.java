package org.example.controller.converter;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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

        var id = Long.parseLong(source);

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
}
