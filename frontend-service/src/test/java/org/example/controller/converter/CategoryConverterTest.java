package org.example.controller.converter;

import org.example.model.products.Category;
import org.example.repository.products.ProductRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class CategoryConverterTest {

    @Autowired
    private CategoryConverter categoryConverter;

    @MockBean
    private ProductRepository productRepository;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    public void shouldReturnNullWhenConvert() {
        var categoryReceived = categoryConverter.convert(null);
        assertNull(categoryReceived);
    }

    @Test
    public void shouldReturnCategoryFromNameWhenConvert() {

        var category = easyRandom.nextObject(Category.class);
        var name = category.getName();

        when(productRepository.getCategoryByName(name)).thenReturn(category);

        var categoryReceived = categoryConverter.convert(name);
        assertNotNull(categoryReceived);
        assertEquals(category, categoryReceived);

        verify(productRepository, times(1)).getCategoryByName(name);
    }

    @Test
    public void shouldReturnNewCategoryWhenConvert() {

        var sourceString = "test category";

        when(productRepository.getAll(any(), any(), any())).thenReturn(Collections.emptyList());

        var categoryReceived = categoryConverter.convert(sourceString);
        assertNotNull(categoryReceived);
        assertNull(categoryReceived.getId());
        assertEquals(sourceString, categoryReceived.getName());
    }
}