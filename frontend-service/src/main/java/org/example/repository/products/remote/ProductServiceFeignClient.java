package org.example.repository.products.remote;

import org.example.model.products.Category;
import org.example.model.products.Product;
import org.example.model.products.ProductRequestDTO;
import org.example.model.users.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${product-service.name}", url = "${product-service.url}")
@ConditionalOnProperty(name = "product-service.enabled", havingValue = "true", matchIfMissing = true)
public interface ProductServiceFeignClient {

    @GetMapping
    ResponseEntity<List<Product>> getAll(@RequestParam(name = "name", required = false) String name,
                                                @RequestParam(name = "category", required = false) Category category,
                                                @RequestParam(name = "user", required = false) User user);

    @GetMapping("/{id}")
    ResponseEntity<Product> getById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<Product> create(@RequestBody ProductRequestDTO productDTO);

    @PutMapping("/{id}")
    ResponseEntity<Product> update(@PathVariable Long id, @RequestBody ProductRequestDTO productDTO);

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteById(@PathVariable Long id);

    @GetMapping("/categories/by-name/{categoryName}")
    ResponseEntity<Category> getCategoryByName(@PathVariable String categoryName);
}
