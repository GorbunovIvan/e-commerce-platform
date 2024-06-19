package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.model.Category;
import org.example.model.DTO.ProductDTO;
import org.example.model.User;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class ProductRepositoryCustomImplTest {

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final EasyRandom easyRandom = new EasyRandom();

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        productRepository.flush();
        entityManager.createQuery("DELETE FROM Category WHERE TRUE").executeUpdate();
        entityManager.createQuery("DELETE FROM User WHERE TRUE").executeUpdate();
        entityManager.flush();
    }

    @Test
    void shouldCreateNewProductWithNewCategoryAndNewUserWhenMerge() {

        var product = easyRandom.nextObject(ProductDTO.class).toProduct();

        var productCreated = productRepository.merge(product);
        assertNotNull(productCreated.getId());
        assertEquals(product, productCreated);
    }

    @Test
    void shouldCreateNewProductWithExistingCategoryAndNewUserWhenMerge() {

        // Persisting category, so in the process of the merging of the product this category will be treated as already existing one
        var categoryExisting = addCategoryToDB(new Category(null, "test-category"));

        var product = easyRandom.nextObject(ProductDTO.class).toProduct();
        product.setCategory(new Category(null, categoryExisting.getName()));

        var productCreated = productRepository.merge(product);
        assertNotNull(productCreated.getId());
        assertEquals(product, productCreated);

        assertNotNull(productCreated.getCategory());
        assertEquals(categoryExisting.getId(), productCreated.getCategory().getId());

        // Checking that no new categories with the same name were persisted
        var numberOfCategories = numberOfCategoriesInDBByName(productCreated.getCategory().getName());
        assertEquals(1L, numberOfCategories);
    }

    @Test
    void shouldCreateNewProductWithNewCategoryAndExistingUserWhenMerge() {

        // Persisting user, so in the process of the merging of the product this user will be treated as already existing one
        var userExisting = addUserToDB(new User(99L));

        var product = easyRandom.nextObject(ProductDTO.class).toProduct();
        product.setUser(new User(userExisting.getId()));

        var productCreated = productRepository.merge(product);
        assertNotNull(productCreated.getId());
        assertEquals(product, productCreated);

        assertNotNull(productCreated.getCategory());
        assertEquals(userExisting.getId(), productCreated.getUser().getId());
    }

    @Test
    void shouldCreateNewProductWithExistingCategoryAndExistingUserWhenMerge() {

        // Persisting category and user, so in the process of the merging of the product this category and this user will be treated as already existing
        var categoryExisting = addCategoryToDB(new Category(null, "test-category"));
        var userExisting = addUserToDB(new User(99L));

        var product = easyRandom.nextObject(ProductDTO.class).toProduct();
        product.setCategory(new Category(null, categoryExisting.getName()));
        product.setUser(new User(userExisting.getId()));

        var productCreated = productRepository.merge(product);
        assertNotNull(productCreated.getId());
        assertEquals(product, productCreated);

        assertNotNull(productCreated.getCategory());
        assertEquals(categoryExisting.getId(), productCreated.getCategory().getId());

        assertNotNull(productCreated.getCategory());
        assertEquals(userExisting.getId(), productCreated.getUser().getId());

        // Checking that no new categories with the same name were persisted
        var numberOfCategories = numberOfCategoriesInDBByName(productCreated.getCategory().getName());
        assertEquals(1L, numberOfCategories);
    }

    private Category addCategoryToDB(Category category) {
        category.setId(null);
        entityManager.persist(category);
        entityManager.flush();
        entityManager.detach(category);
        return new Category(category.getId(), category.getName());
    }

    private User addUserToDB(User user) {
        entityManager.merge(user);
        entityManager.flush();
        entityManager.detach(user);
        return new User(user.getId());
    }

    private int numberOfCategoriesInDBByName(String name) {
        return (int) entityManager.createQuery("FROM Category WHERE name = :name", Category.class)
                .setParameter("name", name)
                .getResultStream()
                .count();
    }
}