package org.example.controller.converter;

import org.example.model.products.Product;
import org.example.repository.products.ProductRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductConverterTest {

    @Autowired
    private ProductConverter productConverter;

    @MockBean
    private ProductRepository productRepository;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    public void shouldReturnNullWhenConvert() {
        var productReceived = productConverter.convert("");
        assertNull(productReceived);
    }

    @Test
    public void shouldReturnProductFromIdWhenConvert() {

        var product = easyRandom.nextObject(Product.class);
        product.setId(7654L);

        var id = product.getId();

        when(productRepository.getById(id)).thenReturn(product);

        var productReceived = productConverter.convert(String.valueOf(id));
        assertNotNull(productReceived);
        assertEquals(product, productReceived);

        verify(productRepository, times(1)).getById(id);
    }

    @Test
    public void shouldReturnNullFromIdWhenConvert() {

        var id = 35L;

        var productReceived = productConverter.convert(String.valueOf(id));
        assertNull(productReceived);

        verify(productRepository, times(1)).getById(id);
    }

    @Test
    public void shouldReturnProductFromUniqueViewWhenConvert() {

        var product = easyRandom.nextObject(Product.class);
        product.setId(87453L);

        var productString = product.getUniqueView();

        when(productRepository.getById(product.getId())).thenReturn(product);

        var productReceived = productConverter.convert(productString);
        assertNotNull(productReceived);
        assertEquals(product, productReceived);

        verify(productRepository, times(1)).getById(product.getId());
    }

    @Test
    public void shouldReturnProductFromNameWhenConvert() {

        var product = easyRandom.nextObject(Product.class);
        var name = product.getName();

        when(productRepository.getAll(name, null, null)).thenReturn(List.of(product));

        var productReceived = productConverter.convert(name);
        assertNotNull(productReceived);
        assertEquals(product, productReceived);

        verify(productRepository, times(1)).getAll(name, null, null);
    }

    @Test
    public void shouldReturnNullFromNameWhenConvert() {

        var product = easyRandom.nextObject(Product.class);
        var name = product.getName();

        var product2 = easyRandom.nextObject(Product.class);
        product2.setName(name);

        var productsToBeFound = List.of(product, product2);
        when(productRepository.getAll(name, null, null)).thenReturn(productsToBeFound);

        var productReceived = productConverter.convert(name);
        assertNull(productReceived);

        verify(productRepository, times(1)).getAll(name, null, null);
    }

    @Test
    public void shouldReturnNewProductWhenConvert() {

        var sourceString = "test product";

        when(productRepository.getAll(any(), any(), any())).thenReturn(Collections.emptyList());

        var productReceived = productConverter.convert(sourceString);
        assertNotNull(productReceived);
        assertNull(productReceived.getId());
        assertEquals(sourceString, productReceived.getName());
    }
}