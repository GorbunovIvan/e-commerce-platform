package org.example.controller;

import org.example.exception.NotFoundException;
import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.orders.StatusTrackerRecord;
import org.example.model.products.Product;
import org.example.service.orders.OrderService;
import org.example.service.orders.StatusTrackerRecordService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private StatusTrackerRecordService statusTrackerRecordService;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnOrdersPageWhenGetAll() throws Exception {

        var ordersExpected = easyRandom.objects(Order.class, 3).toList();

        when(orderService.getAll()).thenReturn(ordersExpected);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/orders"))
                .andExpect(model().attribute("orders", ordersExpected));

        verify(orderService, times(1)).getAll();
    }

    @Test
    void shouldReturnEmptyOrdersPageWhenGetAll() throws Exception {

        when(orderService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/orders"))
                .andExpect(model().attribute("orders", Collections.emptyList()));

        verify(orderService, times(1)).getAll();
    }

    @Test
    void shouldReturnOrderPageWhenGetById() throws Exception {

        var id = "876";

        var orderExpected = easyRandom.nextObject(Order.class);
        orderExpected.setId(id);

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 4).toList();

        when(orderService.getById(id)).thenReturn(orderExpected);
        when(statusTrackerRecordService.getAllByOrder(orderExpected)).thenReturn(statusRecords);

        mockMvc.perform(get("/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/order"))
                .andExpect(model().attribute("order", orderExpected))
                .andExpect(model().attribute("statusHistory", statusRecords))
                .andExpect(model().attribute("statusesAvailable", List.of(Status.values())));

        verify(orderService, times(1)).getById(id);
        verify(statusTrackerRecordService, times(1)).getAllByOrder(orderExpected);
    }

    @Test
    void shouldReturnErrorPageWhenGetById() throws Exception {

        var id = "1";

        mockMvc.perform(get("/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        verify(orderService, times(1)).getById(id);
        verify(statusTrackerRecordService, never()).getAllByOrder(anyString());
        verify(statusTrackerRecordService, never()).getAllByOrder(any(Order.class));
    }

    @Test
    void shouldReturnOrderCreatePageWhenCreatePage() throws Exception {
        mockMvc.perform(get("/orders/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/new"))
                .andExpect(model().attribute("order", new Order()));
    }

    @Test
    void shouldReturnOrderCreatePageWithProductFilledWhenCreatePage() throws Exception {

        var product = new Product();
        product.setName(easyRandom.nextObject(String.class));

        var orderExpected = new Order();
        orderExpected.setProduct(product);
        orderExpected.getProduct().setName(product.getUniqueView());

        mockMvc.perform(get("/orders/new")
                        .param("product", product.getName()))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/new"))
                .andExpect(model().attribute("order", orderExpected));
    }

    @Test
    void shouldCreateAndReturnRedirectionToOrderPageWhenCreate() throws Exception {

        var orderExpected = easyRandom.nextObject(Order.class);

        when(orderService.create(any(Order.class))).thenReturn(orderExpected);

        mockMvc.perform(post("/orders")
                        .param("user", orderExpected.getUserView())
                        .param("product", orderExpected.getProductView()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/orders/" + orderExpected.getId()));

        verify(orderService, times(1)).create(any(Order.class));
    }

    @Test
    void shouldCreateAndReturnRedirectionToOrdersPageWhenCreate() throws Exception {

        var orderExpected = easyRandom.nextObject(Order.class);

        mockMvc.perform(post("/orders")
                        .param("user", orderExpected.getUserView())
                        .param("product", orderExpected.getProductView()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/orders"));

        verify(orderService, times(1)).create(any(Order.class));
    }

    @Test
    void shouldReturnOrderEditPageWhenUpdatePage() throws Exception {

        var id = "734";

        var orderExisting = easyRandom.nextObject(Order.class);
        orderExisting.setId(id);

        when(orderService.getById(id)).thenReturn(orderExisting);

        mockMvc.perform(get("/orders/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/edit"))
                .andExpect(model().attribute("order", orderExisting));

        verify(orderService, times(1)).getById(id);
    }

    @Test
    void shouldReturnErrorPageWhenUpdatePage() throws Exception {

        var id = "54";

        mockMvc.perform(get("/orders/{id}", id))
                .andExpect(view().name("error"));

        verify(orderService, times(1)).getById(id);
    }

    @Test
    void shouldUpdateOrderAndReturnRedirectionToOrderPageWhenUpdate() throws Exception {

        var orderExisting = easyRandom.nextObject(Order.class);
        var id = orderExisting.getId();

        when(orderService.update(anyString(), any(Order.class))).thenReturn(orderExisting);

        mockMvc.perform(patch("/orders/{id}", id)
                        .param("user", orderExisting.getUserView())
                        .param("product", orderExisting.getProductView()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/orders/" + id));

        verify(orderService, times(1)).update(anyString(), any(Order.class));
    }

    @Test
    void shouldReturnErrorPageWhenUpdate() throws Exception {

        var order = easyRandom.nextObject(Order.class);
        var id = order.getId();

        when(orderService.update(anyString(), any(Order.class))).thenThrow(NotFoundException.class);

        mockMvc.perform(patch("/orders/{id}", id)
                        .param("user", order.getUserView())
                        .param("product", order.getProductView()))
                .andExpect(view().name("error"));

        verify(orderService, times(1)).update(anyString(), any(Order.class));
    }

    @Test
    void shouldReturnOrderEditStatusPageWhenChangeOrderStatusPage() throws Exception {

        var id = "734";

        var orderExisting = easyRandom.nextObject(Order.class);
        orderExisting.setId(id);

        when(orderService.getById(id)).thenReturn(orderExisting);

        mockMvc.perform(get("/orders/{id}/edit-status", id))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/edit-status"))
                .andExpect(model().attribute("order", orderExisting))
                .andExpect(model().attribute("statusesAvailable", List.of(Status.values())));

        verify(orderService, times(1)).getById(id);
    }

    @Test
    void shouldReturnErrorPageWhenChangeOrderStatusPage() throws Exception {

        var id = "54";

        mockMvc.perform(get("/orders/{id}/edit-status", id))
                .andExpect(view().name("error"));

        verify(orderService, times(1)).getById(id);
    }

    @Test
    void shouldUpdateOrderAndReturnRedirectionToOrderPageWhenChangeOrderStatus() throws Exception {

        var orderExisting = easyRandom.nextObject(Order.class);
        var id = orderExisting.getId();

        var status = Status.IN_A_WAY;

        mockMvc.perform(post("/orders/{id}/edit-status", id)
                        .param("status", status.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/orders/" + id));

        verify(orderService, times(1)).changeOrderStatus(id, status);
    }

    @Test
    void shouldReturnRedirectionToOrderPageWhenChangeOrderStatus() throws Exception {

        var order = easyRandom.nextObject(Order.class);
        var id = order.getId();

        var status = Status.IN_A_WAY;

        mockMvc.perform(post("/orders/{id}/edit-status", id)
                        .param("status", status.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/orders/" + id));

        verify(orderService, times(1)).changeOrderStatus(id, status);
    }

    @Test
    void shouldDeleteOrderAndReturnRedirectionToOrdersPageWhenDelete() throws Exception {

        var id = "1";

        mockMvc.perform(delete("/orders/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/orders"));

        verify(orderService, times(1)).deleteById(id);
    }
}