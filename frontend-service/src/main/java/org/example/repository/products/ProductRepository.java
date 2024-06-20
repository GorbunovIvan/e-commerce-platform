package org.example.repository.products;

import org.example.model.products.Category;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductRepository {
    List<Product> getAll(String name, Category category, User user);
    Product getById(Long id);
    Product create(Product product);
    Product update(Long id, Product product);
    void deleteById(Long id);
}
