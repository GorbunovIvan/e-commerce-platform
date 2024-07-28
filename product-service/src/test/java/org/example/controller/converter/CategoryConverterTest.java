package org.example.controller.converter;

import org.example.model.Category;
import org.example.repository.CategoryRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class CategoryConverterTest {

    @Autowired
    private CategoryConverter categoryConverter;

    @MockBean
    private CategoryRepository categoryRepository;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnExistingCategoryWhenConvert() {

        var category = easyRandom.nextObject(Category.class);
        var categoryName = category.getName();

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));

        var categoryFound = categoryConverter.convert(categoryName);
        assertNotNull(categoryFound);
        assertNotNull(categoryFound.getId());
        assertEquals(category.getId(), categoryFound.getId());
        assertEquals(category, categoryFound);

        verify(categoryRepository, times(1)).findByName(categoryName);
        verify(categoryRepository, only()).findByName(categoryName);
    }

    @Test
    void shouldReturnNonExistingCategoryWhenConvert() {

        var categoryName = "test-category";
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

        var categoryFound = categoryConverter.convert(categoryName);
        assertNotNull(categoryFound);
        assertNull(categoryFound.getId());
        assertEquals(categoryName, categoryFound.getName());

        verify(categoryRepository, times(1)).findByName(categoryName);
        verify(categoryRepository, only()).findByName(categoryName);
    }

    @Test
    void shouldReturnNullWhenConvert() {

        var categoryFound = categoryConverter.convert(null);
        assertNull(categoryFound);

        verify(categoryRepository, never()).findByName(anyString());
    }
}