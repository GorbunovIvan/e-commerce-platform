package org.example.repository.products.remote;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.products.Category;
import org.example.model.products.Product;
import org.example.model.products.ProductRequestDTO;
import org.example.model.users.User;
import org.example.repository.FeignClientBaseClass;
import org.example.repository.products.ProductRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@Primary
@ConditionalOnProperty(name = "product-service.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class ProductRepositoryBasedOnFeignClient extends FeignClientBaseClass implements ProductRepository {

    private final ProductServiceFeignClient productServiceFeignClient;

    @Override
    protected String getServiceName() {
        return "product-service";
    }

    @Override
    public List<Product> getAll(String name, Category category, User user) {
        log.info("Searching for all products");
        return makeARequest(() -> productServiceFeignClient.getAll(name, category, user), Collections::emptyList);
    }

    @Override
    public Product getById(Long id) {
        log.info("Searching for product with id={}", id);
        return makeARequest(() -> productServiceFeignClient.getById(id));
    }

    @Override
    public List<Product> getByIds(Set<Long> ids) {
        log.info("Searching for products with ids={}", ids);
        return makeARequest(() -> productServiceFeignClient.getByIds(ids), Collections::emptyList);
    }

    @Override
    public Product create(Product product) {
        log.info("Creating product '{}'", product);
        var productRequestDTO = ProductRequestDTO.fromProduct(product);
        return makeARequest(() -> productServiceFeignClient.create(productRequestDTO));
    }

    @Override
    public Product update(Long id, Product product) {
        log.info("Updating product with id={}, {}", id, product);
        var productRequestDTO = ProductRequestDTO.fromProduct(product);
        return makeARequest(() -> productServiceFeignClient.update(id, productRequestDTO));
    }

    @Override
    public void deleteById(Long id) {
        log.warn("Deleting product by id={}", id);
        makeARequest(() -> productServiceFeignClient.deleteById(id));
    }

    @Override
    public Category getCategoryByName(String categoryName) {
        log.info("Searching for category with name={}", categoryName);
        return makeARequest(() -> productServiceFeignClient.getCategoryByName(categoryName));
    }

    @Override
    public List<Category> getCategoriesByNames(Set<String> categoryNames) {
        log.info("Searching for categories with names={}", categoryNames);
        return makeARequest(() -> productServiceFeignClient.getCategoriesByNames(categoryNames));
    }
}
