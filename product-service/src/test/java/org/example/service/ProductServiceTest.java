package org.example.service;

import org.example.model.Category;
import org.example.model.DTO.ProductDTO;
import org.example.model.Product;
import org.example.repository.CategoryRepository;
import org.example.repository.ProductRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.only;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private CategoryRepository categoryRepository;

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
    void shouldReturnListOfProductsWhenGetByIds() {

        var productsExpected = easyRandom.objects(Product.class, 5).toList();
        var ids = productsExpected.stream().map(Product::getId).toList();

        when(productRepository.findAllByIdIn(ids)).thenReturn(productsExpected);

        var products = productService.getByIds(ids);
        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertEquals(productsExpected, products);

        verify(productRepository, times(1)).findAllByIdIn(ids);
        verify(productRepository, only()).findAllByIdIn(ids);
    }

    @Test
    void shouldReturnEmptyListWhenGetByIds() {

        var ids = easyRandom.objects(Long.class, 3).toList();

        when(productRepository.findAllByIdIn(ids)).thenReturn(Collections.emptyList());

        var products = productService.getByIds(ids);
        assertNotNull(products);
        assertTrue(products.isEmpty());

        verify(productRepository, times(1)).findAllByIdIn(ids);
        verify(productRepository, only()).findAllByIdIn(ids);
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

    @Test
    void shouldReturnCategoryWhenGetCategoryByName() {

        var categoryExpected = easyRandom.nextObject(Category.class);
        var name = categoryExpected.getName();

        when(categoryRepository.findByName(name)).thenReturn(Optional.of(categoryExpected));

        var category = productService.getCategoryByName(name);
        assertNotNull(category);
        assertEquals(categoryExpected, category);

        verify(categoryRepository, times(1)).findByName(name);
        verify(categoryRepository, only()).findByName(name);
    }

    @Test
    void shouldReturnNullWhenGetCategoryByName() {

        var name = "test-category";

        when(categoryRepository.findByName(name)).thenReturn(Optional.empty());

        var category = productService.getCategoryByName(name);
        assertNull(category);

        verify(categoryRepository, times(1)).findByName(name);
        verify(categoryRepository, only()).findByName(name);
    }

    @Test
    void shouldReturnListOfCategoriesWhenGetCategoriesByNames() {

        var categoriesExpected = easyRandom.objects(Category.class, 5).toList();
        var names = categoriesExpected.stream().map(Category::getName).collect(Collectors.toCollection(LinkedHashSet::new));

        when(categoryRepository.findAllByNameIn(names)).thenReturn(categoriesExpected);

        var categories = productService.getCategoriesByNames(names);
        assertNotNull(categories);
        assertFalse(categories.isEmpty());
        assertEquals(categoriesExpected, categories);

        verify(categoryRepository, times(1)).findAllByNameIn(names);
        verify(categoryRepository, only()).findAllByNameIn(names);
    }

    @Test
    void shouldReturnEmptyListWhenGetCategoriesByNames() {

        var names = easyRandom.objects(String.class, 3).collect(Collectors.toCollection(LinkedHashSet::new));

        when(categoryRepository.findAllByNameIn(names)).thenReturn(Collections.emptyList());

        var categories = productService.getCategoriesByNames(names);
        assertNotNull(categories);
        assertTrue(categories.isEmpty());

        verify(categoryRepository, times(1)).findAllByNameIn(names);
        verify(categoryRepository, only()).findAllByNameIn(names);
    }
}