package org.example.controller.converter;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Category;
import org.example.repository.CategoryRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryConverter implements Converter<String, Category> {

    private final CategoryRepository categoryRepository;

    @Override
    public Category convert(@Nullable String source) {

        if (source == null) {
            return null;
        }

        log.info("Converting category by name '{}' to object", source);

        var categoryOptional = categoryRepository.findByName(source);
        if (categoryOptional.isPresent()) {
            var category = categoryOptional.get();
            log.info("Category with name '{}' exits - {}", source, category);
            return category;
        }

//        log.info("Saving new category with name '{}'", source);
        var category = new Category();
        category.setName(source);
//        return categoryRepository.save(category);
        return category;
    }
}
