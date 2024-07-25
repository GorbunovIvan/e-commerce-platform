package org.example.repository.products;

import lombok.extern.slf4j.Slf4j;
import org.example.model.products.Category;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class ProductRepositoryDummy implements ProductRepository {

    private final List<Product> products = new ArrayList<>();
    private final Set<Category> categories = new HashSet<>();

    @Override
    public List<Product> getAll(String name, Category category, User user) {

        log.info("Searching for all products");

        var productsStream = products.stream();
        if (name != null) {
            productsStream = productsStream.filter(product -> Objects.equals(product.getName(), name));
        }
        if (category != null) {
            productsStream = productsStream.filter(product -> Objects.equals(product.getCategory(), category));
        }
        if (user != null) {
            productsStream = productsStream.filter(product -> Objects.equals(product.getUser(), user));
        }

        return productsStream
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    @Override
    public Product getById(Long id) {
        log.info("Searching for product with id={}", id);
        return products.stream()
                .filter(product -> Objects.equals(product.getId(), id))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<Product> getByIds(Set<Long> ids) {
        log.info("Searching for products with ids={}", ids);
        return products.stream()
                .filter(product -> ids.contains(product.getId()))
                .toList();
    }

    @Override
    public synchronized Product create(Product product) {

        log.info("Creating product '{}'", product);

        var nextId = nextId();
        product.setId(nextId);

        var category = getCategoryOrAddNew(product.getCategory());
        product.setCategory(category);

        if (product.getCreatedAt() == null) {
            product.setCreatedAt(LocalDateTime.now());
        }

        products.add(product);
        return product;
    }

    @Override
    public synchronized Product update(Long id, Product product) {

        log.info("Updating product with id={}, {}", id, product);

        var productExisting = getById(id);
        if (productExisting == null) {
            log.error("Product with id {} not found", id);
            return null;
        }

        if (product.getName() != null) {
            productExisting.setName(product.getName());
        }
        if (product.getDescription() != null) {
            productExisting.setDescription(product.getDescription());
        }
        if (product.getCategory() != null) {
            var category = getCategoryOrAddNew(product.getCategory());
            productExisting.setCategory(category);
        }
        if (product.getUser() != null) {
            productExisting.setUser(product.getUser());
        }

        return productExisting;
    }

    @Override
    public synchronized void deleteById(Long id) {
        log.warn("Deleting product id={}", id);
        var indexOfProductInList = getIndexOfProductInListById(id);
        if (indexOfProductInList == -1) {
            return;
        }
        products.remove(indexOfProductInList);
    }

    @Override
    public Category getCategoryByName(String categoryName) {
        log.info("Searching for category with name={}", categoryName);
        return categories.stream()
                .filter(category -> Objects.equals(category.getName(), categoryName))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<Category> getCategoriesByNames(Set<String> categoryNames) {
        log.info("Searching for categories with names={}", categoryNames);
        return categories.stream()
                .filter(category -> categoryNames.contains(category.getName()))
                .toList();
    }

    private Long nextId() {
        return products.stream()
                .mapToLong(Product::getId)
                .max()
                .orElse(0) + 1;
    }

    private int getIndexOfProductInListById(Long id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private Category getCategoryOrAddNew(Category category) {

        if (category == null) {
            return null;
        }

        var categoryFound = getCategoryByName(category.getName());
        if (categoryFound != null) {
            return categoryFound;
        }

        log.info("Category with name={} not found, adding new", category.getName());

        var newCategoryId = categories.stream()
                .mapToInt(Category::getId)
                .max()
                .orElse(0) + 1;

        category.setId(newCategoryId);

        categories.add(category);
        return category;
    }
}
