package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.exception.NotFoundException;
import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.example.service.orders.OrderService;
import org.example.service.orders.StatusTrackerRecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final StatusTrackerRecordService statusTrackerRecordService;

    @GetMapping
    public String getAll(Model model) {
        var orders = orderService.getAll();
        model.addAttribute("orders", orders);
        return "orders/orders";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable String id) {
        var order = orderService.getById(id);
        if (order == null) {
            throw new NotFoundException(String.format("Order with id=%s not found", id));
        }
        model.addAttribute("order", order);
        model.addAttribute("statusHistory", statusTrackerRecordService.getAllByOrder(order));
        model.addAttribute("statusesAvailable", List.of(Status.values()));
        return "orders/order";
    }

    @GetMapping("/new")
    public String createPage(Model model, @RequestParam(required = false) Product product) {

        var order = new Order();

        if (product != null) {
            order.setProduct(product);
        }

        var currentUser = getCurrentUserFromModel(model);
        order.setUser(currentUser);

        model.addAttribute("order", order);
        return "orders/new";
    }

    @PostMapping
    public String create(@ModelAttribute Order order, Model model) {
        var currentUser = getCurrentUserFromModel(model);
        order.setUser(currentUser);
        var orderCreated = orderService.create(order);
        if (orderCreated == null) { // In the case of asynchronous creation
            return "redirect:/orders";
        }
        return "redirect:/orders/" + orderCreated.getId();
    }

    @GetMapping("/{id}/edit")
    public String updatePage(@PathVariable String id, Model model) {

        var order = orderService.getById(id);
        if (order == null) {
            throw new NotFoundException(String.format("Order with id=%s not found", id));
        }

        var currentUser = getCurrentUserFromModel(model);
        if (currentUser == null || !currentUser.equals(order.getUser())) {
            throw new RuntimeException("You are not allowed to edit this order");
        }

        model.addAttribute("order", order);
        return "orders/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable String id, @ModelAttribute Order order) {
        orderService.update(id, order);
        return "redirect:/orders/" + id;
    }

    @GetMapping("/{id}/edit-status")
    public String changeOrderStatusPage(@PathVariable String id, Model model) {

        var order = orderService.getById(id);
        if (order == null) {
            throw new NotFoundException(String.format("Order with id=%s not found", id));
        }

        var currentUser = getCurrentUserFromModel(model);
        if (currentUser == null || !currentUser.equals(order.getUser())) {
            throw new RuntimeException("You are not allowed to edit the status of this order");
        }

        model.addAttribute("order", order);
        model.addAttribute("statusesAvailable", List.of(Status.values()));
        return "orders/edit-status";
    }

    @PostMapping("/{id}/edit-status")
    public String changeOrderStatus(@PathVariable String id, @ModelAttribute Status status) {
        orderService.changeOrderStatus(id, status);
        return "redirect:/orders/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable String id) {
        orderService.deleteById(id);
        return "redirect:/orders";
    }

    private User getCurrentUserFromModel(Model model) {
        var currentUserAttribute = model.getAttribute("currentUser");
        if (currentUserAttribute == null) {
            return null;
        }
        if (currentUserAttribute instanceof User currentUser) {
            return currentUser;
        }
        return null;
    }
}
