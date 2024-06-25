package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.exception.NotFoundException;
import org.example.model.users.User;
import org.example.service.orders.OrderService;
import org.example.service.products.ProductService;
import org.example.service.reviews.ReviewService;
import org.example.service.users.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final ProductService productService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    @GetMapping
    public String getAll(Model model) {
        var users = userService.getAll();
        model.addAttribute("users", users);
        return "users/users";
    }

    @GetMapping("/{idOrUsername}")
    public String getById(Model model, @PathVariable String idOrUsername) {
        var user = getUserByParam(idOrUsername);
        if (user == null) {
            throw new NotFoundException(String.format("User '%s' not found", idOrUsername));
        }
        model.addAttribute("user", user);
        model.addAttribute("products", productService.getAll(null, null, user));
        model.addAttribute("orders", orderService.getAllByUser(user));
        model.addAttribute("reviews", reviewService.getAllByUser(user));
        return "users/user";
    }

    @PostMapping
    public String create(@ModelAttribute User user) {
        var userCreated = userService.create(user);
        return "redirect:/users/" + userCreated.getId();
    }

    @GetMapping("/{id}/edit")
    public String updatePage(@PathVariable Long id, Model model) {
        var user = userService.getById(id);
        if (user == null) {
            throw new NotFoundException(String.format("User with id=%s not found", id));
        }
        model.addAttribute("user", user);
        return "users/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute User user) {
        userService.update(id, user);
        return "redirect:/users/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/users";
    }

    private User getUserByParam(String param) {

        try {
            long id = Long.parseUnsignedLong(param);
            if (id > 0) {
                return userService.getById(id);
            }
        } catch (NumberFormatException ignored) {}

        return userService.getByUsername(param);
    }
}
