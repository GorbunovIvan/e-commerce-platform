package org.example.repository.products;

import lombok.extern.slf4j.Slf4j;
import org.example.model.products.Category;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProductRepositoryDummy implements ProductRepository {

    private final List<Product> products = new ArrayList<>();

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

        return productsStream.toList();
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
    public Product create(Product product) {
        log.info("Creating product '{}'", product);
        var nextId = nextId();
        product.setId(nextId);
        products.add(product);
        return product;
    }

    @Override
    public synchronized Product update(Long id, Product product) {
        log.info("Updating product with id={}, {}", id, product);
        var indexOfProductInList = getIndexOfProductInListById(id);
        if (indexOfProductInList == -1) {
            log.error("Product with id {} not found", id);
            return null;
        }
        product.setId(id);
        products.set(indexOfProductInList, product);
        return product;
    }

    @Override
    public synchronized void deleteById(Long id) {
        log.warn("Deleting product id={}", id);
        var indexOfProductInList = getIndexOfProductInListById(id);
        products.remove(indexOfProductInList);
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
}
