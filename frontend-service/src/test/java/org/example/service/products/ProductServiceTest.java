package org.example.service.products;

import org.example.exception.NotFoundException;
import org.example.model.products.Product;
import org.example.repository.products.ProductRepository;
import org.example.service.modelsBinding.ModelBinder;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private ModelBinder modelBinder;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        when(modelBinder.bindFields(any())).thenAnswer(ans -> ans.getArgument(0));
    }

    @Test
    void shouldReturnProductWhenGetById() {

        var productExpected = easyRandom.nextObject(Product.class);
        var id = productExpected.getId();

        when(productRepository.getById(id)).thenReturn(productExpected);

        var product = productService.getById(id);
        assertNotNull(product);
        assertEquals(productExpected, product);

        verify(productRepository, times(1)).getById(id);
        verify(modelBinder, times(1)).bindFields(productExpected);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = 1L;

        when(productRepository.getById(anyLong())).thenReturn(null);

        var product = productService.getById(id);
        assertNull(product);

        verify(productRepository, times(1)).getById(id);
        verify(modelBinder, times(1)).bindFields(null);
    }

    @Test
    void shouldReturnListOfProductsWhenGetAll() {

        var productsExpected = easyRandom.objects(Product.class, 5).toList();
        when(productRepository.getAll(null, null, null)).thenReturn(productsExpected);

        var products = productService.getAll();
        assertNotNull(products);
        assertEquals(productsExpected, products);

        verify(productRepository, times(1)).getAll(null, null, null);
        verify(modelBinder, times(1)).bindFields(productsExpected);
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {

        when(productRepository.getAll(null, null, null)).thenReturn(Collections.emptyList());

        var products = productService.getAll();
        assertNotNull(products);
        assertTrue(products.isEmpty());

        verify(productRepository, times(1)).getAll(null, null, null);
        verify(modelBinder, times(1)).bindFields(Collections.emptyList());
    }

    @Test
    void shouldReturnListOfProductsWhenGetAllByExample() {

        var productsExpected = easyRandom.objects(Product.class, 5).toList();
        var firstProduct = productsExpected.getFirst();

        when(productRepository.getAll(firstProduct.getName(), firstProduct.getCategory(), firstProduct.getUser())).thenReturn(productsExpected);

        var productsRetrieved = productService.getAll(firstProduct.getName(), firstProduct.getCategory(), firstProduct.getUser());
        assertNotNull(productsRetrieved);
        assertEquals(productsExpected, productsRetrieved);

        verify(productRepository, times(1)).getAll(firstProduct.getName(), firstProduct.getCategory(), firstProduct.getUser());
        verify(modelBinder, times(1)).bindFields(productsExpected);
    }

    @Test
    void shouldCreateAndReturnNewProductWhenCreate() {

        var product = easyRandom.nextObject(Product.class);

        when(productRepository.create(product)).thenReturn(product);

        var productResult = productService.create(product);
        assertNotNull(productResult);
        assertEquals(product, productResult);

        verify(productRepository, times(1)).create(product);
        verify(modelBinder, times(1)).bindFields(product);
    }

    @Test
    void shouldUpdateAndReturnProductWhenUpdate() {

        var productExisting = easyRandom.nextObject(Product.class);
        var id = productExisting.getId();

        when(productRepository.update(id, productExisting)).thenReturn(productExisting);

        var productUpdated = productService.update(id, productExisting);
        assertNotNull(productUpdated);
        assertEquals(id, productUpdated.getId());
        assertEquals(productExisting, productUpdated);

        verify(productRepository, times(1)).update(id, productExisting);
        verify(modelBinder, times(1)).bindFields(productExisting);
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var id = 1L;

        var product = easyRandom.nextObject(Product.class);

        when(productRepository.update(id, product)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> productService.update(id, product));

        verify(productRepository, times(1)).update(id, product);
        verify(modelBinder, never()).bindFields(any());
    }

    @Test
    void shouldDeleteProductWhenDelete() {
        var id = 1L;
        productService.deleteById(id);
        verify(productRepository, times(1)).deleteById(id);
    }
}