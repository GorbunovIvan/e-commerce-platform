package org.example.service;

import org.example.model.DTO.ProductDTO;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnProductWhenGetById() {

        var productExpected = easyRandom.nextObject(Product.class);
        var id = productExpected.getId();

        when(productRepository.findById(id)).thenReturn(Optional.of(productExpected));

        var product = productService.getById(id);
        assertNotNull(product);
        assertEquals(productExpected, product);

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, only()).findById(id);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = 1L;

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        var product = productService.getById(id);
        assertNull(product);

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, only()).findById(id);
    }

    @Test
    void shouldReturnListOfProductsWhenGetAll() {

        var productsExpected = easyRandom.objects(Product.class, 5).toList();
        when(productRepository.findAll()).thenReturn(productsExpected);

        var products = productService.getAll();
        assertNotNull(products);
        assertEquals(productsExpected, products);

        verify(productRepository, times(1)).findAll();
        verify(productRepository, only()).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {

        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        var products = productService.getAll();
        assertNotNull(products);
        assertTrue(products.isEmpty());

        verify(productRepository, times(1)).findAll();
        verify(productRepository, only()).findAll();
    }

    @Test
    void shouldReturnListOfProductsWhenGetByAllByExample() {

        var products = easyRandom.objects(Product.class, 5).toList();

        for (var product : products) {
            when(productRepository.findAll(Example.of(product))).thenReturn(List.of(product));
        }

        for (var product : products) {

            var productsRetrieved = productService.getAllByExample(product);

            assertNotNull(productsRetrieved);
            assertEquals(1, productsRetrieved.size());
            assertEquals(product, productsRetrieved.getFirst());

            verify(productRepository, atLeast(1)).findAll(Example.of(product));
        }

        //noinspection unchecked
        verify(productRepository, times(products.size())).findAll(any(Example.class));
    }

    @Test
    void shouldCreateAndReturnNewProductWhenCreate() {

        when(productRepository.merge(any(Product.class))).thenAnswer(answer -> {
            Product product = answer.getArgument(0);
            product.setId(99L);
            return product;
        });

        var productDTO = easyRandom.nextObject(ProductDTO.class);
        productDTO.setCreatedAt(null);

        var product = productService.create(productDTO);
        assertNotNull(product);
        assertNotNull(product.getId());
        assertNotNull(product.getCreatedAt());
        assertEquals(productDTO.getName(), product.getName());
        assertEquals(productDTO.getDescription(), product.getDescription());
        assertEquals(productDTO.getCategoryObj(), product.getCategory());
        assertEquals(productDTO.getUser(), product.getUser());

        verify(productRepository, times(1)).merge(any(Product.class));
        verify(productRepository, only()).merge(any(Product.class));
    }

    @Test
    void shouldUpdateAndReturnProductWhenUpdate() {

        var productExisting = easyRandom.nextObject(Product.class);
        var id = productExisting.getId();

        when(productRepository.findById(id)).thenReturn(Optional.of(productExisting));
        when(productRepository.merge(any(Product.class))).thenAnswer(answer -> answer.getArgument(0));

        var productDTO = easyRandom.nextObject(ProductDTO.class);

        var productUpdated = productService.update(id, productDTO);
        assertNotNull(productUpdated);
        assertEquals(id, productUpdated.getId());
        assertEquals(productExisting, productUpdated);
        assertEquals(productExisting.getDescription(), productUpdated.getDescription());

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).merge(any(Product.class));
    }

    @Test
    void shouldUpdateAndReturnProductWhenUpdateWithoutChanging() {

        var productExisting = easyRandom.nextObject(Product.class);
        var id = productExisting.getId();

        when(productRepository.findById(id)).thenReturn(Optional.of(productExisting));
        when(productRepository.merge(any(Product.class))).thenAnswer(answer -> answer.getArgument(0));

        var productDTO = new ProductDTO(); // Empty, so no field in existing product should be changed

        var productUpdated = productService.update(id, productDTO);
        assertNotNull(productUpdated);
        assertEquals(id, productUpdated.getId());
        assertEquals(productExisting, productUpdated);
        assertEquals(productExisting.getDescription(), productUpdated.getDescription());

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).merge(any(Product.class));
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var id = 99L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        var productDTO = easyRandom.nextObject(ProductDTO.class);

        var productUpdated = productService.update(id, productDTO);
        assertNull(productUpdated);

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, never()).merge(any(Product.class));
    }

    @Test
    void shouldDeleteProductWhenDelete() {

        var id = 1L;

        productService.deleteById(id);

        verify(productRepository, times(1)).deleteById(id);
        verify(productRepository, only()).deleteById(id);
    }
}