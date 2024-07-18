package org.example.repository.products;

import org.example.model.products.Category;
import org.example.model.products.Product;
import org.example.repository.ReflectUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryDummyTest {

    @Autowired
    private ProductRepositoryDummy productRepositoryDummy;

    @Autowired
    private ReflectUtil reflectUtil;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        setProductsToProductRepositoryDummy(new ArrayList<>());
        setCategoriesToProductRepositoryDummy(new HashSet<>());
    }

    @Test
    void shouldReturnProductWhenGetById() {

        var productExpected = easyRandom.nextObject(Product.class);
        setProductsToProductRepositoryDummy(List.of(productExpected));
        
        var id = productExpected.getId();

        var product = productRepositoryDummy.getById(id);
        assertNotNull(product);
        assertEquals(productExpected, product);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var productsExisting = easyRandom.objects(Product.class, 5).toList();
        setProductsToProductRepositoryDummy(new ArrayList<>(productsExisting));

        var id = 1L;

        var product = productRepositoryDummy.getById(id);
        assertNull(product);
    }

    @Test
    void shouldReturnListOfProductsWhenGetByIds() {

        var productsExpected = easyRandom.objects(Product.class, 5).toList();
        setProductsToProductRepositoryDummy(new ArrayList<>(productsExpected));

        var ids = productsExpected.stream().map(Product::getId).collect(Collectors.toSet());

        var products = productRepositoryDummy.getByIds(ids);
        assertNotNull(products);
        assertEquals(getProductsFromProductRepositoryDummy(), products);
    }

    @Test
    void shouldReturnEmptyListWhenGetByIds() {

        var productsExpected = easyRandom.objects(Product.class, 5).toList();
        setProductsToProductRepositoryDummy(new ArrayList<>(productsExpected));

        var ids = easyRandom.objects(Long.class, 3).collect(Collectors.toSet());

        var products = productRepositoryDummy.getByIds(ids);
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void shouldReturnListOfProductsWhenGetAll() {

        var productsExpected = easyRandom.objects(Product.class, 5).toList();
        setProductsToProductRepositoryDummy(new ArrayList<>(productsExpected));

        var products = productRepositoryDummy.getAll(null, null, null);
        assertNotNull(products);
        assertEquals(productsExpected, products);
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {
        var products = productRepositoryDummy.getAll(null, null, null);
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void shouldReturnListOfProductsWhenGetAllByExample() {

        var productsExisting = easyRandom.objects(Product.class, 5).toList();
        setProductsToProductRepositoryDummy(new ArrayList<>(productsExisting));

        var firstProduct = productsExisting.getFirst();

        var productsRetrieved = productRepositoryDummy.getAll(firstProduct.getName(), firstProduct.getCategory(), firstProduct.getUser());
        assertNotNull(productsRetrieved);
        assertEquals(List.of(firstProduct), productsRetrieved);
    }

    @Test
    void shouldCreateAndReturnNewProductWhenCreate() {

        var productsExisting = easyRandom.objects(Product.class, 5).toList();
        setProductsToProductRepositoryDummy(new ArrayList<>(productsExisting));

        var product = easyRandom.nextObject(Product.class);

        var productResult = productRepositoryDummy.create(product);
        assertNotNull(productResult);
        assertEquals(product, productResult);

        var productsAfterOperation = getProductsFromProductRepositoryDummy();
        assertTrue(productsAfterOperation.contains(productResult));
        assertEquals(productsExisting.size() + 1, productsAfterOperation.size());
    }

    @Test
    void shouldUpdateAndReturnProductWhenUpdate() {

        var productsExisting = easyRandom.objects(Product.class, 5).toList();
        setProductsToProductRepositoryDummy(new ArrayList<>(productsExisting));

        var productExisting = productsExisting.getFirst();
        var id = productExisting.getId();

        var productUpdated = productRepositoryDummy.update(id, productExisting);
        assertNotNull(productUpdated);
        assertEquals(id, productUpdated.getId());
        assertEquals(productExisting, productUpdated);

        var productsAfterOperation = getProductsFromProductRepositoryDummy();
        assertTrue(productsAfterOperation.contains(productUpdated));
        assertEquals(productsExisting.size(), productsAfterOperation.size());
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var productsExisting = easyRandom.objects(Product.class, 5).toList();
        setProductsToProductRepositoryDummy(new ArrayList<>(productsExisting));

        var id = 1L;

        var product = easyRandom.nextObject(Product.class);

        assertNull(productRepositoryDummy.update(id, product));

        var productsAfterOperation = getProductsFromProductRepositoryDummy();
        assertEquals(productsExisting.size(), productsAfterOperation.size());
    }

    @Test
    void shouldDeleteProductWhenDelete() {

        var productsExisting = easyRandom.objects(Product.class, 5).toList();
        setProductsToProductRepositoryDummy(new ArrayList<>(productsExisting));

        var productToDelete = productsExisting.getFirst();
        var id = productToDelete.getId();

        productRepositoryDummy.deleteById(id);

        var productsAfterOperation = getProductsFromProductRepositoryDummy();
        assertFalse(productsAfterOperation.contains(productToDelete));
        assertEquals(productsExisting.size() - 1, productsAfterOperation.size());
    }

    @Test
    void shouldNotDeleteProductWhenDelete() {

        var productsExisting = easyRandom.objects(Product.class, 5).toList();
        setProductsToProductRepositoryDummy(new ArrayList<>(productsExisting));

        var id = 55L;

        productRepositoryDummy.deleteById(id);

        var productsAfterOperation = getProductsFromProductRepositoryDummy();
        assertEquals(productsExisting.size(), productsAfterOperation.size());
    }

    @Test
    void shouldReturnCategoryWhenGetCategoryByName() {

        var categoryExpected = easyRandom.nextObject(Category.class);
        setCategoriesToProductRepositoryDummy(Set.of(categoryExpected));

        var name = categoryExpected.getName();

        var category = productRepositoryDummy.getCategoryByName(name);
        assertNotNull(category);
        assertEquals(categoryExpected, category);
    }

    @Test
    void shouldReturnNullWhenGetCategoryByName() {

        var categoryExpected = easyRandom.nextObject(Category.class);
        setCategoriesToProductRepositoryDummy(Set.of(categoryExpected));

        var name = "-1";

        var category = productRepositoryDummy.getCategoryByName(name);
        assertNull(category);
    }

    @Test
    void shouldReturnListOfCategoriesWhenGetCategoriesByNames() {

        var categoriesExpected = easyRandom.objects(Category.class, 5).toList();
        setCategoriesToProductRepositoryDummy(new HashSet<>(categoriesExpected));

        var names = categoriesExpected.stream().map(Category::getName).collect(Collectors.toSet());

        var categories = productRepositoryDummy.getCategoriesByNames(names);
        assertNotNull(categories);
        assertEquals(getCategoriesFromProductRepositoryDummy(), new HashSet<>(categories));
    }

    @Test
    void shouldReturnEmptyListWhenGetCategoriesByNames() {

        var categoriesExpected = easyRandom.objects(Category.class, 5).toList();
        setCategoriesToProductRepositoryDummy(new HashSet<>(categoriesExpected));

        var names = easyRandom.objects(String.class, 3).collect(Collectors.toSet());

        var categories = productRepositoryDummy.getCategoriesByNames(names);
        assertNotNull(categories);
        assertTrue(categories.isEmpty());
    }

    private List<Product> getProductsFromProductRepositoryDummy() {
        return reflectUtil.getValueOfObjectField(productRepositoryDummy, "products", Collections::emptyList);
    }

    private void setProductsToProductRepositoryDummy(List<Product> products) {
        reflectUtil.setValueToObjectField(productRepositoryDummy, "products", products);
    }

    private Set<Category> getCategoriesFromProductRepositoryDummy() {
        return reflectUtil.getValueOfObjectField(productRepositoryDummy, "categories", Collections::emptySet);
    }

    private void setCategoriesToProductRepositoryDummy(Set<Category> categories) {
        reflectUtil.setValueToObjectField(productRepositoryDummy, "categories", categories);
    }
}