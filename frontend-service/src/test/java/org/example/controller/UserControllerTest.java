package org.example.controller;

import org.example.exception.NotFoundException;
import org.example.model.orders.Order;
import org.example.model.products.Product;
import org.example.model.reviews.Review;
import org.example.model.users.User;
import org.example.service.orders.OrderService;
import org.example.service.products.ProductService;
import org.example.service.reviews.ReviewService;
import org.example.service.users.UserService;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ProductService productService;
    @MockBean
    private OrderService orderService;
    @MockBean
    private ReviewService reviewService;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnUsersPageWhenGetAll() throws Exception {

        var usersExpected = easyRandom.objects(User.class, 3).toList();

        when(userService.getAll()).thenReturn(usersExpected);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/users"))
                .andExpect(model().attribute("users", usersExpected));

        verify(userService, times(1)).getAll();
    }

    @Test
    void shouldReturnEmptyUsersPageWhenGetAll() throws Exception {

        when(userService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/users"))
                .andExpect(model().attribute("users", Collections.emptyList()));

        verify(userService, times(1)).getAll();
    }

    @Test
    void shouldReturnUserPageWhenGetById() throws Exception {

        var id = 234L;

        var userExpected = easyRandom.nextObject(User.class);
        userExpected.setId(id);

        var products = easyRandom.objects(Product.class, 3).toList();
        var orders = easyRandom.objects(Order.class, 4).toList();
        var reviews = easyRandom.objects(Review.class, 5).toList();

        when(userService.getById(id)).thenReturn(userExpected);
        when(productService.getAll(null, null, userExpected)).thenReturn(products);
        when(orderService.getAllByUser(userExpected)).thenReturn(orders);
        when(reviewService.getAllByUser(userExpected)).thenReturn(reviews);

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("users/user"))
                .andExpect(model().attribute("user", userExpected))
                .andExpect(model().attribute("products", products))
                .andExpect(model().attribute("orders", orders))
                .andExpect(model().attribute("reviews", reviews));

        verify(userService, times(1)).getById(id);
        verify(productService, times(1)).getAll(null, null, userExpected);
        verify(orderService, times(1)).getAllByUser(userExpected);
        verify(reviewService, times(1)).getAllByUser(userExpected);
    }

    @Test
    void shouldReturnErrorPageWhenGetById() throws Exception {

        var id = 1L;

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        verify(userService, times(1)).getById(id);
        verify(productService, never()).getAll(any(), any(), any());
        verify(orderService, never()).getAllByUser(any(User.class));
        verify(reviewService, never()).getAllByUser(any(User.class));
    }

    @Test
    void shouldReturnUserPageWhenGetByUsername() throws Exception {

        var userExpected = easyRandom.nextObject(User.class);
        var username = userExpected.getUsername();

        var products = easyRandom.objects(Product.class, 3).toList();
        var orders = easyRandom.objects(Order.class, 4).toList();
        var reviews = easyRandom.objects(Review.class, 5).toList();

        when(userService.getByUsername(username)).thenReturn(userExpected);
        when(productService.getAll(null, null, userExpected)).thenReturn(products);
        when(orderService.getAllByUser(userExpected)).thenReturn(orders);
        when(reviewService.getAllByUser(userExpected)).thenReturn(reviews);

        mockMvc.perform(get("/users/{idOrUsername}", username))
                .andExpect(status().isOk())
                .andExpect(view().name("users/user"))
                .andExpect(model().attribute("user", userExpected))
                .andExpect(model().attribute("products", products))
                .andExpect(model().attribute("orders", orders))
                .andExpect(model().attribute("reviews", reviews));

        verify(userService, times(1)).getByUsername(username);
        verify(productService, times(1)).getAll(null, null, userExpected);
        verify(orderService, times(1)).getAllByUser(userExpected);
        verify(reviewService, times(1)).getAllByUser(userExpected);
    }

    @Test
    void shouldReturnErrorPageWhenGetByUsername() throws Exception {

        var username = "username-of-user";

        mockMvc.perform(get("/users/{idOrUsername}", username))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        verify(userService, times(1)).getByUsername(username);
        verify(productService, never()).getAll(any(), any(), any());
        verify(orderService, never()).getAllByUser(any(User.class));
        verify(reviewService, never()).getAllByUser(any(User.class));
    }

    @Test
    void shouldCreateAndReturnRedirectionToUserPageWhenCreate() throws Exception {

        var userExpected = easyRandom.nextObject(User.class);

        when(userService.create(any())).thenReturn(userExpected);

        mockMvc.perform(post("/users")
                        .param("username", userExpected.getUsername()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/" + userExpected.getId()));

        verify(userService, times(1)).create(userExpected);
    }

    @Test
    void shouldReturnUserEditPageWhenUpdatePage() throws Exception {

        var id = 345L;

        var userExisting = easyRandom.nextObject(User.class);
        userExisting.setId(id);

        when(userService.getById(id)).thenReturn(userExisting);

        mockMvc.perform(get("/users/{id}/edit", id)
                        .flashAttr("currentUser", userExisting))
                .andExpect(status().isOk())
                .andExpect(view().name("users/edit"))
                .andExpect(model().attribute("user", userExisting));

        verify(userService, times(1)).getById(id);
    }

    @Test
    void shouldReturnErrorPageForUnauthorizedUserWhenUpdatePage() throws Exception {

        var id = 345L;

        var userExisting = easyRandom.nextObject(User.class);
        userExisting.setId(id);

        when(userService.getById(id)).thenReturn(userExisting);

        mockMvc.perform(get("/users/{id}/edit", id))
                .andExpect(view().name("error"));

        verify(userService, times(1)).getById(id);
    }

    @Test
    void shouldReturnErrorPageWhenUpdatePage() throws Exception {

        var id = 45L;

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(view().name("error"));

        verify(userService, times(1)).getById(id);
    }

    @Test
    void shouldUpdateUserAndReturnRedirectionToUserPageWhenUpdate() throws Exception {

        var userExisting = easyRandom.nextObject(User.class);
        var id = userExisting.getId();

        when(userService.update(id, userExisting)).thenReturn(userExisting);

        mockMvc.perform(patch("/users/{id}", id)
                        .param("username", userExisting.getUsername()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/" + userExisting.getId()));

        verify(userService, times(1)).update(id, userExisting);
    }

    @Test
    void shouldReturnErrorPageWhenUpdate() throws Exception {

        var user = easyRandom.nextObject(User.class);
        var id = user.getId();

        when(userService.update(id, user)).thenThrow(NotFoundException.class);

        mockMvc.perform(patch("/users/{id}", id)
                        .param("username", user.getUsername()))
                .andExpect(view().name("error"));

        verify(userService, times(1)).update(id, user);
    }

    @Test
    void shouldDeleteUserAndReturnRedirectionToUsersPageWhenDelete() throws Exception {

        var id = 1L;

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/logout"));

        verify(userService, times(1)).deleteById(id);
    }
}