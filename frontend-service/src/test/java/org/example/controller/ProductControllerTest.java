package org.example.controller;

import org.example.exception.NotFoundException;
import org.example.model.orders.Order;
import org.example.model.products.Product;
import org.example.model.reviews.ProductAndRatingInfo;
import org.example.model.reviews.Review;
import org.example.service.orders.OrderService;
import org.example.service.products.ProductService;
import org.example.service.reviews.ReviewService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private OrderService orderService;
    @MockBean
    private ReviewService reviewService;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnProductsPageWhenGetAll() throws Exception {

        var productsExpected = easyRandom.objects(Product.class, 3).toList();

        when(productService.getAll()).thenReturn(productsExpected);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/products"))
                .andExpect(model().attribute("products", productsExpected));

        verify(productService, times(1)).getAll();
    }

    @Test
    void shouldReturnEmptyProductsPageWhenGetAll() throws Exception {

        when(productService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/products"))
                .andExpect(model().attribute("products", Collections.emptyList()));

        verify(productService, times(1)).getAll();
    }

    @Test
    void shouldReturnProductPageWhenGetById() throws Exception {

        var id = 234L;

        var productExpected = easyRandom.nextObject(Product.class);
        productExpected.setId(id);

        var orders = easyRandom.objects(Order.class, 4).toList();
        var reviews = easyRandom.objects(Review.class, 5).toList();
        var productAndRatingInfo = new ProductAndRatingInfo(productExpected, reviews);

        when(productService.getById(id)).thenReturn(productExpected);
        when(orderService.getAllByProduct(productExpected)).thenReturn(orders);
        when(reviewService.getRatingInfoOfProduct(productExpected)).thenReturn(productAndRatingInfo);

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product"))
                .andExpect(model().attribute("product", productExpected))
                .andExpect(model().attribute("orders", orders))
                .andExpect(model().attribute("productAndRatingInfo", productAndRatingInfo));

        verify(productService, times(1)).getById(id);
        verify(orderService, times(1)).getAllByProduct(productExpected);
        verify(reviewService, times(1)).getRatingInfoOfProduct(productExpected);
    }

    @Test
    void shouldReturnErrorPageWhenGetById() throws Exception {

        var id = 1L;

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        verify(productService, times(1)).getById(id);
        verify(productService, never()).getAll(any(), any(), any());
        verify(orderService, never()).getAllByProduct(any(Product.class));
        verify(reviewService, never()).getRatingInfoOfProduct(any(Product.class));
    }

    @Test
    void shouldReturnProductCreatePageWhenCreatePage() throws Exception {
        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/new"))
                .andExpect(model().attribute("product", new Product()));
    }

    @Test
    void shouldCreateAndReturnRedirectionToProductPageWhenCreate() throws Exception {

        var productExpected = easyRandom.nextObject(Product.class);

        when(productService.create(any(Product.class))).thenReturn(productExpected);

        mockMvc.perform(post("/products")
                        .param("name", productExpected.getName())
                        .param("description", productExpected.getDescription())
                        .param("category", productExpected.getCategoryName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products/" + productExpected.getId()));

        verify(productService, times(1)).create(any(Product.class));
    }

    @Test
    void shouldReturnProductEditPageWhenUpdatePage() throws Exception {

        var id = 345L;

        var productExisting = easyRandom.nextObject(Product.class);
        productExisting.setId(id);

        when(productService.getById(id)).thenReturn(productExisting);

        mockMvc.perform(get("/products/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(view().name("products/edit"))
                .andExpect(model().attribute("product", productExisting));

        verify(productService, times(1)).getById(id);
    }

    @Test
    void shouldReturnErrorPageWhenUpdatePage() throws Exception {

        var id = 45L;

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(view().name("error"));

        verify(productService, times(1)).getById(id);
    }

    @Test
    void shouldUpdateProductAndReturnRedirectionToProductPageWhenUpdate() throws Exception {

        var productExisting = easyRandom.nextObject(Product.class);
        var id = productExisting.getId();

        when(productService.update(anyLong(), any(Product.class))).thenReturn(productExisting);

        mockMvc.perform(patch("/products/{id}", id)
                        .param("name", productExisting.getName())
                        .param("description", productExisting.getDescription())
                        .param("category", productExisting.getCategoryName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products/" + productExisting.getId()));

        verify(productService, times(1)).update(anyLong(), any(Product.class));
    }

    @Test
    void shouldReturnErrorPageWhenUpdate() throws Exception {

        var product = easyRandom.nextObject(Product.class);
        var id = product.getId();

        when(productService.update(anyLong(), any(Product.class))).thenThrow(NotFoundException.class);

        mockMvc.perform(patch("/products/{id}", id)
                        .param("name", product.getName())
                        .param("description", product.getDescription())
                        .param("category", product.getCategoryName()))
                .andExpect(view().name("error"));

        verify(productService, times(1)).update(anyLong(), any(Product.class));
    }

    @Test
    void shouldDeleteProductAndReturnRedirectionToProductsPageWhenDelete() throws Exception {

        var id = 1L;

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products"));

        verify(productService, times(1)).deleteById(id);
    }
}