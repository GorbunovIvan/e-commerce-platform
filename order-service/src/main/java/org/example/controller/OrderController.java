package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Order;
import org.example.service.OrderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @QueryMapping
    public Order getOrderById(@Argument String id) {
        return orderService.getById(id);
    }

    @QueryMapping
    public List<Order> getAllOrders() {
        return orderService.getAll();
    }

    @QueryMapping
    public List<Order> getAllOrdersByUser(@Argument Long userId) {
        return orderService.getAllByUser(userId);
    }

    @QueryMapping
    public List<Order> getAllOrdersByProduct(@Argument Long productId) {
        return orderService.getAllByProduct(productId);
    }
}
