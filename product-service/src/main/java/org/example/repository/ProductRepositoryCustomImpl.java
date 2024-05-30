package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Category;
import org.example.model.Product;
import org.example.model.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final CategoryRepository categoryRepository;

    @Override
    public Product merge(@NotNull Product product) {

        if (entityManager.contains(product)) {
            entityManager.detach(product);
        }

        log.info("Merging product - {}", product);

        var category = findOrCreateCategory(product.getCategory());
        var user = findOrCreateUser(product.getUser());

        product.setCategory(category);
        product.setUser(user);

        return entityManager.merge(product);
    }

    private Category findOrCreateCategory(Category category) {
        if (category == null) {
            return null;
        }

        var categoryOptional = categoryRepository.findByName(category.getName());
        if (categoryOptional.isPresent()) {
            log.info("Category with name '{}' already exists - {}", category.getName(), categoryOptional.get());
            return categoryOptional.get();
        }

        log.info("Saving new category - {}", category);
        return categoryRepository.save(category);
    }

    private User findOrCreateUser(User user) {
        if (user == null) {
            return null;
        }

        var userFound = entityManager.find(User.class, user.getId());
        if (userFound != null) {
            log.info("User with id '{}' already exists - {}", user.getId(), user);
            return userFound;
        }

        log.info("Saving new user - {}", user);
        return entityManager.merge(user);
    }
}
