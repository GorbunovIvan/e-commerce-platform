package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Category;
import org.example.model.DTO.ProductDTO;
import org.example.model.Product;
import org.example.model.User;
import org.example.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAll(@RequestParam(name = "name", required = false) String name,
                                                @RequestParam(name = "category", required = false) Category category,
                                                @RequestParam(name = "user", required = false) User user) {
        if (name == null && category == null && user == null) {
            var products = productService.getAll();
            return ResponseEntity.ok(products);
        }

        var productExample = new Product();
        productExample.setName(name);
        productExample.setCategory(category);
        productExample.setUser(user);

        var products = productService.getAllByExample(productExample);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        var product = productService.getById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping("/ids/{ids}")
    public ResponseEntity<List<Product>> getByIds(@PathVariable Collection<Long> ids) {
        var products = productService.getByIds(ids);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductDTO productDTO) {
        var product = productService.create(productDTO);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        var product = productService.update(id, productDTO);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteById(@PathVariable Long id) {
        productService.deleteById(id);
    }

    @GetMapping("/categories/by-name/{categoryName}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String categoryName) {
        var category = productService.getCategoryByName(categoryName);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    @GetMapping("/categories/by-names/{categoryNames}")
    public ResponseEntity<List<Category>> getCategoriesByNames(@PathVariable Collection<String> categoryNames) {
        var categories = productService.getCategoriesByNames(categoryNames);
        return ResponseEntity.ok(categories);
    }
}
