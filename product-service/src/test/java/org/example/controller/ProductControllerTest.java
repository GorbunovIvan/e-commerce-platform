package org.example.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import org.example.model.DTO.ProductDTO;
import org.example.model.Product;
import org.example.service.ProductService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.only;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom easyRandom = new EasyRandom();

    @PostConstruct
    void init() {
        objectMapper.findAndRegisterModules();

        RestAssured.baseURI = "http://localhost/api/v1/products";
        RestAssured.port = port;
    }

    @Test
    void shouldReturnListOfProductsWhenGetAll() throws Exception {

        var productsExpected = easyRandom.objects(Product.class, 5).toList();

        when(productService.getAll()).thenReturn(productsExpected);

        var jsonResponse =
                RestAssured.when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        List<Product> products = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(products);
        assertEquals(productsExpected, products);

        verify(productService, times(1)).getAll();
        verify(productService, only()).getAll();
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() throws Exception {

        when(productService.getAll()).thenReturn(Collections.emptyList());

        var jsonResponse =
                RestAssured.when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        List<Product> products = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(products);
        assertTrue(products.isEmpty());

        verify(productService, times(1)).getAll();
        verify(productService, only()).getAll();
    }

    @Test
    void shouldReturnListOfProductsByNameWhenGetAll() throws Exception {

        var productExpected = easyRandom.nextObject(Product.class);

        var productExample = new Product();
        productExample.setName(productExpected.getName());

        when(productService.getAllByExample(productExample)).thenReturn(List.of(productExpected));

        var jsonResponse =
                given()
                        .param("name", productExpected.getName())
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        List<Product> products = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(products);
        assertEquals(List.of(productExpected), products);

        verify(productService, times(1)).getAllByExample(productExample);
        verify(productService, only()).getAllByExample(productExample);
    }

    @Test
    void shouldReturnEmptyListByNameWhenGetAll() throws Exception {

        var productExpected = easyRandom.nextObject(Product.class);

        var productExample = new Product();
        productExample.setName(productExpected.getName());

        when(productService.getAllByExample(productExample)).thenReturn(Collections.emptyList());

        var jsonResponse =
                given()
                        .param("name", productExpected.getName())
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        List<Product> products = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(products);
        assertTrue(products.isEmpty());

        verify(productService, times(1)).getAllByExample(productExample);
        verify(productService, only()).getAllByExample(productExample);
    }

    @Test
    void shouldReturnListOfProductsByCategoryWhenGetAll() throws Exception {

        var productExpected = easyRandom.nextObject(Product.class);

        var productExample = new Product();
        productExample.setCategory(productExpected.getCategory());

        when(productService.getAllByExample(productExample)).thenReturn(List.of(productExpected));

        var jsonResponse =
                given()
                        .param("category", productExpected.getCategory().getName())
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        List<Product> products = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(products);
        assertEquals(List.of(productExpected), products);

        verify(productService, times(1)).getAllByExample(productExample);
        verify(productService, only()).getAllByExample(productExample);
    }

    @Test
    void shouldReturnEmptyListByCategoryWhenGetAll() throws Exception {

        var productExpected = easyRandom.nextObject(Product.class);

        var productExample = new Product();
        productExample.setCategory(productExpected.getCategory());

        when(productService.getAllByExample(productExample)).thenReturn(Collections.emptyList());

        var jsonResponse =
                given()
                        .param("category", productExpected.getCategory().getName())
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        List<Product> products = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(products);
        assertTrue(products.isEmpty());

        verify(productService, times(1)).getAllByExample(productExample);
        verify(productService, only()).getAllByExample(productExample);
    }

    @Test
    void shouldReturnListOfProductsByUserWhenGetAll() throws Exception {

        var productExpected = easyRandom.nextObject(Product.class);

        var productExample = new Product();
        productExample.setUser(productExpected.getUser());

        when(productService.getAllByExample(productExample)).thenReturn(List.of(productExpected));

        var jsonResponse =
                given()
                        .param("user", productExpected.getUser().getId().toString())
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        List<Product> products = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(products);
        assertEquals(List.of(productExpected), products);

        verify(productService, times(1)).getAllByExample(productExample);
        verify(productService, only()).getAllByExample(productExample);
    }

    @Test
    void shouldReturnEmptyListByUserWhenGetAll() throws Exception {

        var productExpected = easyRandom.nextObject(Product.class);

        var productExample = new Product();
        productExample.setUser(productExpected.getUser());

        when(productService.getAllByExample(any(Product.class))).thenReturn(Collections.emptyList());

        var jsonResponse =
                given()
                        .param("user", productExpected.getUser().getId().toString())
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        List<Product> products = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(products);
        assertTrue(products.isEmpty());

        verify(productService, times(1)).getAllByExample(productExample);
        verify(productService, only()).getAllByExample(productExample);
    }

    @Test
    void shouldReturnListOfProductsByNameCategoryAndUserWhenGetAll() throws Exception {

        var productExpected = easyRandom.nextObject(Product.class);

        var productExample = new Product();
        productExample.setName(productExpected.getName());
        productExample.setCategory(productExpected.getCategory());
        productExample.setUser(productExpected.getUser());

        when(productService.getAllByExample(productExample)).thenReturn(List.of(productExpected));

        var jsonResponse =
                given()
                        .param("name", productExpected.getName())
                        .param("category", productExpected.getCategory().getName())
                        .param("user", productExpected.getUser().getId().toString())
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        List<Product> products = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(products);
        assertEquals(List.of(productExpected), products);

        verify(productService, times(1)).getAllByExample(productExample);
        verify(productService, only()).getAllByExample(productExample);
    }

    @Test
    void shouldReturnEmptyListByNameCategoryAndUserWhenGetAll() throws Exception {

        var productExpected = easyRandom.nextObject(Product.class);

        var productExample = new Product();
        productExample.setName(productExpected.getName());
        productExample.setCategory(productExpected.getCategory());
        productExample.setUser(productExpected.getUser());

        when(productService.getAllByExample(productExample)).thenReturn(Collections.emptyList());

        var jsonResponse =
                given()
                        .param("name", productExpected.getName())
                        .param("category", productExpected.getCategory().getName())
                        .param("user", productExpected.getUser().getId().toString())
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        List<Product> products = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(products);
        assertTrue(products.isEmpty());

        verify(productService, times(1)).getAllByExample(productExample);
        verify(productService, only()).getAllByExample(productExample);
    }

    @Test
    void shouldReturnProductWhenGetById() throws Exception {

        var productExpected = easyRandom.nextObject(Product.class);
        var id = productExpected.getId();

        when(productService.getById(id)).thenReturn(productExpected);

        var jsonResponse =
                RestAssured.when()
                        .get("/{id}", id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        Product product = objectMapper.readValue(jsonResponse, Product.class);

        assertNotNull(product);
        assertEquals(productExpected, product);

        verify(productService, times(1)).getById(id);
        verify(productService, only()).getById(id);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = 95L;

        RestAssured.when()
                .get("/{id}", id)
                .then()
                .statusCode(404);

        verify(productService, times(1)).getById(id);
        verify(productService, only()).getById(id);
    }

    @Test
    void shouldCreateAndReturnNewProductWhenCreate() throws Exception {

        when(productService.create(any(ProductDTO.class))).thenAnswer(answer -> {
            ProductDTO productDTO = answer.getArgument(0);
            Product product = productDTO.toProduct();
            product.setId(99L);
            if (product.getCreatedAt() == null) {
                product.setCreatedAt(LocalDateTime.now());
            }
            return product;
        });

        var productDTO = easyRandom.nextObject(ProductDTO.class);
        var jsonProductDTO = objectMapper.writeValueAsString(productDTO);

        var jsonResponse =
                given()
                        .contentType("application/json")
                        .body(jsonProductDTO)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        Product product = objectMapper.readValue(jsonResponse, Product.class);

        assertNotNull(product);
        assertNotNull(product.getId());
        assertNotNull(product.getCreatedAt());
        assertEquals(productDTO.getDescription(), product.getDescription());
        assertEquals(productDTO.toProduct(), product);

        verify(productService, times(1)).create(any(ProductDTO.class));
        verify(productService, only()).create(any(ProductDTO.class));
    }

    @Test
    void shouldUpdateAndReturnProductWhenUpdate() throws Exception {

        var productExisting = easyRandom.nextObject(Product.class);
        var id = productExisting.getId();

        when(productService.getById(id)).thenReturn(productExisting);
        when(productService.update(anyLong(), any(ProductDTO.class))).thenAnswer(answer -> {
            Long idParam = answer.getArgument(0);
            ProductDTO productDTO = answer.getArgument(1);
            var product = productDTO.toProduct();
            product.setId(idParam);
            return product;
        });

        var productDTO = easyRandom.nextObject(ProductDTO.class);
        var jsonProductDTO = objectMapper.writeValueAsString(productDTO);

        var jsonResponse =
                given()
                        .contentType("application/json")
                        .body(jsonProductDTO)
                        .when()
                        .patch("/{id}", id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        Product productUpdated = objectMapper.readValue(jsonResponse, Product.class);

        assertNotNull(productUpdated);
        assertEquals(id, productUpdated.getId());
        assertEquals(productDTO.getDescription(), productUpdated.getDescription());
        assertEquals(productDTO.getCreatedAt(), productUpdated.getCreatedAt());
        assertEquals(productDTO.toProduct(), productUpdated);

        verify(productService, times(1)).update(anyLong(), any(ProductDTO.class));
        verify(productService, only()).update(anyLong(), any(ProductDTO.class));
    }

    @Test
    void shouldReturnNullWhenUpdate() throws Exception {

        var id = 98L;

        var productDTO = easyRandom.nextObject(ProductDTO.class);
        var jsonProductDTO = objectMapper.writeValueAsString(productDTO);

        given()
                .contentType("application/json")
                .body(jsonProductDTO)
                .when()
                .patch("/{id}", id)
                .then()
                .statusCode(404);

        verify(productService, times(1)).update(anyLong(), any(ProductDTO.class));
        verify(productService, only()).update(anyLong(), any(ProductDTO.class));
    }

    @Test
    void shouldDeleteProductWhenDelete() {

        var id = 77L;

        given()
                .when()
                .delete("/{id}", id)
                .then()
                .statusCode(202);

        verify(productService, times(1)).deleteById(id);
        verify(productService, only()).deleteById(id);
    }
}