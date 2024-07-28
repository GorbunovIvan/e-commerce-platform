package org.example.repository.products.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.example.model.products.Category;
import org.example.model.products.Product;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WireMockTest
@EnableFeignClients
@TestPropertySource(properties = "product-service.enabled=true")
class ProductRepositoryBasedOnFeignClientTest {

    @Autowired
    private ProductRepositoryBasedOnFeignClient productRepositoryBasedOnFeignClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom easyRandom = new EasyRandom();

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void setUpMockBaseURL(DynamicPropertyRegistry registry) {

        var remoteServerHost = getHostFromWireMockBaseURL();
        var remoteServerPort = wireMockExtension.getPort();
        var remoteServerAddress = "http://" + remoteServerHost + ":" + remoteServerPort + "/api/v1/products";

        registry.add("product-service.url", () -> remoteServerAddress);
    }

    @BeforeAll
    static void beforeAll() {
        configureFor(getHostFromWireMockBaseURL(), wireMockExtension.getPort());
    }

    private static String getHostFromWireMockBaseURL() {
        try {
            return (new URI(wireMockExtension.baseUrl())).getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldReturnServiceNameWhenGetServiceName() {
        var serviceNameExpected = "product-service";
        var result = productRepositoryBasedOnFeignClient.getServiceName();
        assertEquals(serviceNameExpected, result);
    }

    @Test
    void shouldReturnProductWhenGetById() throws JsonProcessingException {

        var productExpected = easyRandom.nextObject(Product.class);
        var id = productExpected.getId();

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(productExpected);

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/products/" + id))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var product = productRepositoryBasedOnFeignClient.getById(id);
        assertNotNull(product);
        assertEquals(productExpected, product);

        verify(getRequestedFor(urlEqualTo("/api/v1/products/" + id)));
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = 1L;

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/products/" + id))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(404)));

        var product = productRepositoryBasedOnFeignClient.getById(id);
        assertNull(product);

        verify(getRequestedFor(urlEqualTo("/api/v1/products/" + id)));
    }

    @Test
    void shouldReturnProductsWhenGetByIds() throws JsonProcessingException {

        var productsExpected = easyRandom.objects(Product.class, 3).toList();
        var ids = productsExpected.stream().map(Product::getId).collect(Collectors.toSet());

        var idsRequest = ids.stream().map(String::valueOf).collect(Collectors.joining(","));

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(productsExpected);

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/products/ids/" + idsRequest))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var products = productRepositoryBasedOnFeignClient.getByIds(ids);
        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertEquals(productsExpected, products);

        verify(getRequestedFor(urlEqualTo("/api/v1/products/ids/" + idsRequest)));
    }

    @Test
    void shouldReturnEmptyListWhenGetByIds() throws JsonProcessingException {

        var productsExpected = Collections.emptyList();
        Set<Long> ids = Collections.emptySet();

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(productsExpected);

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/products/ids/"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var products = productRepositoryBasedOnFeignClient.getByIds(ids);
        assertNotNull(products);
        assertTrue(products.isEmpty());

        verify(getRequestedFor(urlEqualTo("/api/v1/products/ids/")));
    }
    
    @Test
    void shouldReturnListOfProductsWhenGetAll() throws JsonProcessingException {

        var productsExpected = easyRandom.objects(Product.class, 3).toList();
        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(productsExpected);

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/products"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var productsReceived = productRepositoryBasedOnFeignClient.getAll(null, null, null);
        assertNotNull(productsReceived);
        assertEquals(productsExpected, productsReceived);

        verify(getRequestedFor(urlEqualTo("/api/v1/products")));
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() throws JsonProcessingException {

        var productsExpected = Collections.emptyList();
        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(productsExpected);

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/products"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var products = productRepositoryBasedOnFeignClient.getAll(null, null, null);
        assertNotNull(products);
        assertTrue(products.isEmpty());

        verify(getRequestedFor(urlEqualTo("/api/v1/products")));
    }

    @Test
    void shouldReturnListOfProductsByParamsWhenGetAll() throws JsonProcessingException {

        var productsExpected = easyRandom.objects(Product.class, 5).toList();
        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(productsExpected);

        var firstProduct = productsExpected.getFirst();

        var urlPathMatching = urlPathMatching("/api/v1/products");

        // Stubbing remote-service
        stubFor(get(urlPathMatching)
                .withQueryParam("name", matching(firstProduct.getName()))
                .withQueryParam("category", matching(".+"))
                .withQueryParam("user", matching(".+"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var products = productRepositoryBasedOnFeignClient.getAll(firstProduct.getName(),
                firstProduct.getCategory(),
                firstProduct.getUser());

        assertNotNull(products);
        assertEquals(productsExpected, products);

        verify(getRequestedFor(urlPathMatching));
    }

    @Test
    void shouldCreateAndReturnNewProductWhenCreate() throws JsonProcessingException {

        var productExpected = easyRandom.nextObject(Product.class);

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(productExpected);

        // Stubbing remote-service
        stubFor(post(urlEqualTo("/api/v1/products"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var product = productRepositoryBasedOnFeignClient.create(productExpected);
        assertNotNull(product);
        assertEquals(productExpected, product);

        verify(postRequestedFor(urlEqualTo("/api/v1/products")));
    }

    @Test
    void shouldUpdateAndReturnProductWhenUpdate() throws JsonProcessingException {

        var productExisting = easyRandom.nextObject(Product.class);
        var id = productExisting.getId();

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(productExisting);

        // Stubbing remote-service
        stubFor(put(urlEqualTo("/api/v1/products/" + id))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var productUpdated = productRepositoryBasedOnFeignClient.update(id, productExisting);
        assertNotNull(productUpdated);
        assertEquals(id, productUpdated.getId());
        assertEquals(productExisting, productUpdated);

        verify(putRequestedFor(urlEqualTo("/api/v1/products/" + id)));
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var id = 1L;

        var product = easyRandom.nextObject(Product.class);

        // Stubbing remote-service
        stubFor(put(urlEqualTo("/api/v1/products/" + id))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(404)));

        var productUpdated = productRepositoryBasedOnFeignClient.update(id, product);
        assertNull(productUpdated);

        verify(putRequestedFor(urlEqualTo("/api/v1/products/" + id)));
    }

    @Test
    void shouldDeleteProductWhenDelete() {

        var id = 1L;

        // Stubbing remote-service
        stubFor(delete(urlEqualTo("/api/v1/products/" + id))
                .willReturn(aResponse()
                        .withStatus(200)));

        productRepositoryBasedOnFeignClient.deleteById(id);

        verify(deleteRequestedFor(urlEqualTo("/api/v1/products/" + id)));
    }

    @Test
    void shouldReturnCategoryWhenGetCategoryByName() throws JsonProcessingException {

        var categoryExpected = easyRandom.nextObject(Category.class);
        var name = categoryExpected.getName();

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(categoryExpected);

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/products/categories/by-name/" + name))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var category = productRepositoryBasedOnFeignClient.getCategoryByName(name);
        assertNotNull(category);
        assertEquals(categoryExpected, category);

        verify(getRequestedFor(urlEqualTo("/api/v1/products/categories/by-name/" + name)));
    }

    @Test
    void shouldReturnNullWhenGetCategoryByName() {

        var name = "some-name";

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/products/categories/by-name/" + name))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(404)));

        var category = productRepositoryBasedOnFeignClient.getCategoryByName(name);
        assertNull(category);

        verify(getRequestedFor(urlEqualTo("/api/v1/products/categories/by-name/" + name)));
    }

    @Test
    void shouldReturnCategoriesWhenGetCategoriesByNames() throws JsonProcessingException {

        var categoriesExpected = easyRandom.objects(Category.class, 3).toList();
        var names = categoriesExpected.stream().map(Category::getName).collect(Collectors.toSet());

        var namesRequest = names.stream().map(String::valueOf).collect(Collectors.joining(","));

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(categoriesExpected);

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/products/categories/by-names/" + namesRequest))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var categories = productRepositoryBasedOnFeignClient.getCategoriesByNames(names);
        assertNotNull(categories);
        assertFalse(categories.isEmpty());
        assertEquals(categoriesExpected, categories);

        verify(getRequestedFor(urlEqualTo("/api/v1/products/categories/by-names/" + namesRequest)));
    }

    @Test
    void shouldReturnEmptyListWhenGetCategoriesByNames() throws JsonProcessingException {

        var productsExpected = Collections.emptyList();
        Set<String> names = Collections.emptySet();

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(productsExpected);

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/products/categories/by-names/"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var categories = productRepositoryBasedOnFeignClient.getCategoriesByNames(names);
        assertNotNull(categories);
        assertTrue(categories.isEmpty());

        verify(getRequestedFor(urlEqualTo("/api/v1/products/categories/by-names/")));
    }
}