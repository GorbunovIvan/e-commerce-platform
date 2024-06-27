package org.example.repository.products.remote;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.products.Category;
import org.example.model.products.Product;
import org.example.model.products.ProductRequestDTO;
import org.example.model.users.User;
import org.example.repository.products.ProductRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Primary
@ConditionalOnProperty(name = "product-service.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class ProductRepositoryBasedOnFeignClient implements ProductRepository {

    private final ProductServiceFeignClient productServiceFeignClient;

    @Override
    public List<Product> getAll(String name, Category category, User user) {
        log.info("Searching for all products");
        var response = productServiceFeignClient.getAll(name, category, user);
        if (response.getStatusCode().isError()) {
            logRemoteServiceError(response);
            return Collections.emptyList();
        }
        return response.getBody();
    }

    @Override
    public Product getById(Long id) {
        log.info("Searching for product with id={}", id);
        var response = productServiceFeignClient.getById(id);
        if (response.getStatusCode().isError()) {
            logRemoteServiceError(response);
            return null;
        }
        return response.getBody();
    }

    @Override
    public Product create(Product product) {
        log.info("Creating product '{}'", product);
        var productRequestDTO = ProductRequestDTO.fromProduct(product);
        var response = productServiceFeignClient.create(productRequestDTO);
        if (response.getStatusCode().isError()) {
            logRemoteServiceError(response);
            return null;
        }
        return response.getBody();
    }

    @Override
    public Product update(Long id, Product product) {
        log.info("Updating product with id={}, {}", id, product);
        var productRequestDTO = ProductRequestDTO.fromProduct(product);
        var response = productServiceFeignClient.update(id, productRequestDTO);
        if (response.getStatusCode().isError()) {
            logRemoteServiceError(response);
            return null;
        }
        return response.getBody();
    }

    @Override
    public void deleteById(Long id) {
        log.warn("Deleting product by id={}", id);
        var response = productServiceFeignClient.deleteById(id);
        if (response.getStatusCode().isError()) {
            logRemoteServiceError(response);
        }
    }

    @Override
    public Category getCategoryByName(String categoryName) {
        log.info("Searching for category with name={}", categoryName);
        var response = productServiceFeignClient.getCategoryByName(categoryName);
        if (response.getStatusCode().isError()) {
            logRemoteServiceError(response);
            return null;
        }
        return response.getBody();
    }

    private void logRemoteServiceError(ResponseEntity<?> response) {
        var errorTitle = "Remote product-service is not available";
        log.error("{}. {} - {}", errorTitle, response.getStatusCode(), response.getBody());
    }
}
