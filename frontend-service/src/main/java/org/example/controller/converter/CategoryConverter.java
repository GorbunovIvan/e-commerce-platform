package org.example.controller.converter;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.products.Category;
import org.example.repository.products.ProductRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryConverter implements Converter<String, Category> {

    private final ProductRepository productRepository;

    @Override
    public Category convert(@Nullable String source) {

        if (source == null) {
            return null;
        }

        log.info("Converting category by name '{}' to object", source);

        var categoryFound = productRepository.getCategoryByName(source);
        if (categoryFound != null) {
            log.info("Category with name '{}' exits - {}", source, categoryFound);
            return categoryFound;
        }

        var category = new Category();
        category.setName(source);
        return category;
    }
}
