package org.example.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.DTO.ProductDTO;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public Product getById(@NotNull Long id) {
        log.info("Searching for product with id={}", id);
        return productRepository.findById(id)
                .orElse(null);
    }

    public List<Product> getAll() {
        log.info("Searching for all products");
        return productRepository.findAll();
    }

    public List<Product> getAllByExample(Product product) {
        log.info("Searching for products by example={}", product);
        return productRepository.findAll(Example.of(product));
    }

    @Transactional
    public Product create(@NotNull ProductDTO productDTO) {

        log.info("Creating product '{}'", productDTO);

        var product = productDTO.toProduct();
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(LocalDateTime.now());
        }

        return productRepository.merge(product);
    }

    @Transactional
    public Product update(@NotNull Long id, @NotNull ProductDTO productDTO) {

        log.info("Updating product with id={}, {}", id, productDTO);

        var productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            log.error("Product with id={} not found", id);
            return null;
        }

        var product = productOptional.get();

        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }
        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }
        if (productDTO.getCategory() != null) {
            product.setCategory(productDTO.getCategoryObj());
        }
        if (productDTO.getUser() != null) {
            product.setUser(productDTO.getUser());
        }

        return productRepository.merge(product);
    }

    public void deleteById(@NotNull Long id) {
        log.warn("Deleting product with id={}", id);
        productRepository.deleteById(id);
    }
}
