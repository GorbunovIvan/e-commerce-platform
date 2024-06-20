package org.example.service.products;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.products.Category;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.example.repository.products.ProductRepositoryDummy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepositoryDummy productRepository;

    public List<Product> getAll() {
        log.info("Searching for all products");
        return productRepository.getAll(null, null, null);
    }

    public List<Product> getAll(String name, Category category, User user) {
        log.info("Searching for products by params");
        return productRepository.getAll(name, category, user);
    }

    public Product getById(Long id) {
        log.info("Searching for product with id={}", id);
        return productRepository.getById(id);
    }

    public Product create(Product product) {
        log.info("Creating product '{}'", product);
        return productRepository.create(product);
    }

    public Product update(Long id, Product product) {
        log.info("Updating product with id={}, {}", id, product);
        return productRepository.update(id, product);
    }

    public void deleteById(Long id) {
        log.warn("Deleting product with id={}", id);
        productRepository.deleteById(id);
    }
}
