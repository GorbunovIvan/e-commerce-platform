package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Order;
import org.example.model.Status;
import org.example.model.dto.OrderDTO;
import org.example.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable String id) {
        var order = orderService.getById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        var orders = orderService.getAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getAllByUser(@PathVariable Long userId) {
        var orders = orderService.getAllByUser(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Order>> getAllByProduct(@PathVariable Long productId) {
        var orders = orderService.getAllByProduct(productId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/create-from-bytes")
    public ResponseEntity<Order> create(byte[] orderMessage) {
        var order = orderService.create(orderMessage);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/create-from-json")
    public ResponseEntity<Order> create(String orderMessage) {
        var order = orderService.create(orderMessage);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody OrderDTO orderDTO) {
        var order = orderService.create(orderDTO);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable String id, @RequestBody OrderDTO orderDTO) {
        var order = orderService.update(id, orderDTO);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/{status}")
    public ResponseEntity<Order> changeOrderStatus(@PathVariable String id, @PathVariable Status status) {
        var order = orderService.changeOrderStatus(id, status);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        orderService.deleteById(id);
    }
}
