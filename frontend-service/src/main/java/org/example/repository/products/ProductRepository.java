package org.example.repository.products;

import org.example.model.products.Category;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface ProductRepository {

    List<Product> getAll(String name, Category category, User user);
    Product getById(Long id);
    List<Product> getByIds(Set<Long> ids);
    Product create(Product product);
    Product update(Long id, Product product);
    void deleteById(Long id);

    Category getCategoryByName(String categoryName);
    List<Category> getCategoriesByNames(Set<String> categoryNames);
}
