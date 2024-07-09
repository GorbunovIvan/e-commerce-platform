package org.example.service.products;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.NotFoundException;
import org.example.model.products.Category;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.example.repository.products.ProductRepository;
import org.example.service.modelsBinding.ModelBinder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelBinder modelBinder;

    public List<Product> getAll() {
        log.info("Searching for all products");
        var result = productRepository.getAll(null, null, null);
        return modelBinder.bindFields(result);
    }

    public List<Product> getAll(String name, Category category, User user) {
        log.info("Searching for products by params");
        var result = productRepository.getAll(name, category, user);
        return modelBinder.bindFields(result);
    }

    public Product getById(Long id) {
        log.info("Searching for product with id={}", id);
        var result = productRepository.getById(id);
        return modelBinder.bindFields(result);
    }

    public Product create(Product product) {
        log.info("Creating product '{}'", product);
        if (product.getUser() == null) {
            //TODO - must be filled with current user
            product.setUser(new User(999L, null));
        }
        var result = productRepository.create(product);
        return modelBinder.bindFields(result);
    }

    public Product update(Long id, Product product) {
        log.info("Updating product with id={}, {}", id, product);
        var productUpdated = productRepository.update(id, product);
        if (productUpdated == null) {
            throw new NotFoundException(String.format("Product with id=%s not found", id));
        }
        return modelBinder.bindFields(productUpdated);
    }

    public void deleteById(Long id) {
        log.warn("Deleting product with id={}", id);
        productRepository.deleteById(id);
    }
}
