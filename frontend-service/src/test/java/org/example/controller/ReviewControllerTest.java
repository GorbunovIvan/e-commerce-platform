package org.example.controller;

import org.example.exception.NotFoundException;
import org.example.model.products.Product;
import org.example.model.reviews.Review;
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
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnReviewsPageWhenGetAll() throws Exception {

        var reviewsExpected = easyRandom.objects(Review.class, 3).toList();

        when(reviewService.getAll()).thenReturn(reviewsExpected);

        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk())
                .andExpect(view().name("reviews/reviews"))
                .andExpect(model().attribute("reviews", reviewsExpected));

        verify(reviewService, times(1)).getAll();
    }

    @Test
    void shouldReturnEmptyReviewsPageWhenGetAll() throws Exception {

        when(reviewService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk())
                .andExpect(view().name("reviews/reviews"))
                .andExpect(model().attribute("reviews", Collections.emptyList()));

        verify(reviewService, times(1)).getAll();
    }

    @Test
    void shouldReturnReviewPageWhenGetById() throws Exception {

        var id = "876";

        var reviewExpected = easyRandom.nextObject(Review.class);
        reviewExpected.setId(id);

        when(reviewService.getById(id)).thenReturn(reviewExpected);

        mockMvc.perform(get("/reviews/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("reviews/review"))
                .andExpect(model().attribute("review", reviewExpected));

        verify(reviewService, times(1)).getById(id);
    }

    @Test
    void shouldReturnErrorPageWhenGetById() throws Exception {

        var id = "1";

        mockMvc.perform(get("/reviews/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        verify(reviewService, times(1)).getById(id);
    }

    @Test
    void shouldReturnReviewCreatePageWhenCreatePage() throws Exception {
        mockMvc.perform(get("/reviews/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("reviews/new"))
                .andExpect(model().attribute("review", new Review()));
    }

    @Test
    void shouldReturnReviewCreatePageWithProductFilledWhenCreatePage() throws Exception {

        var product = new Product();
        product.setName(easyRandom.nextObject(String.class));

        var reviewExpected = new Review();
        reviewExpected.setProduct(product);
        reviewExpected.getProduct().setName(product.getUniqueView());

        mockMvc.perform(get("/reviews/new")
                        .param("product", product.getName()))
                .andExpect(status().isOk())
                .andExpect(view().name("reviews/new"))
                .andExpect(model().attribute("review", reviewExpected));
    }

    @Test
    void shouldCreateAndReturnRedirectionToReviewPageWhenCreate() throws Exception {

        var reviewExpected = easyRandom.nextObject(Review.class);

        when(reviewService.create(any(Review.class))).thenReturn(reviewExpected);

        mockMvc.perform(post("/reviews")
                        .param("user", reviewExpected.getUserView())
                        .param("product", reviewExpected.getProductView()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/reviews/" + reviewExpected.getId()));

        verify(reviewService, times(1)).create(any(Review.class));
    }

    @Test
    void shouldReturnReviewEditPageWhenUpdatePage() throws Exception {

        var id = "734";

        var reviewExisting = easyRandom.nextObject(Review.class);
        reviewExisting.setId(id);

        when(reviewService.getById(id)).thenReturn(reviewExisting);

        mockMvc.perform(get("/reviews/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(view().name("reviews/edit"))
                .andExpect(model().attribute("review", reviewExisting));

        verify(reviewService, times(1)).getById(id);
    }

    @Test
    void shouldReturnErrorPageWhenUpdatePage() throws Exception {

        var id = "54";

        mockMvc.perform(get("/reviews/{id}", id))
                .andExpect(view().name("error"));

        verify(reviewService, times(1)).getById(id);
    }

    @Test
    void shouldUpdateReviewAndReturnRedirectionToReviewPageWhenUpdate() throws Exception {

        var reviewExisting = easyRandom.nextObject(Review.class);
        var id = reviewExisting.getId();

        when(reviewService.update(anyString(), any(Review.class))).thenReturn(reviewExisting);

        mockMvc.perform(patch("/reviews/{id}", id)
                        .param("user", reviewExisting.getUserView())
                        .param("product", reviewExisting.getProductView()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/reviews/" + id));

        verify(reviewService, times(1)).update(anyString(), any(Review.class));
    }

    @Test
    void shouldReturnErrorPageWhenUpdate() throws Exception {

        var review = easyRandom.nextObject(Review.class);
        var id = review.getId();

        when(reviewService.update(anyString(), any(Review.class))).thenThrow(NotFoundException.class);

        mockMvc.perform(patch("/reviews/{id}", id)
                        .param("user", review.getUserView())
                        .param("product", review.getProductView()))
                .andExpect(view().name("error"));

        verify(reviewService, times(1)).update(anyString(), any(Review.class));
    }

    @Test
    void shouldDeleteReviewAndReturnRedirectionToReviewsPageWhenDelete() throws Exception {

        var id = "1";

        mockMvc.perform(delete("/reviews/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/reviews"));

        verify(reviewService, times(1)).deleteById(id);
    }
}