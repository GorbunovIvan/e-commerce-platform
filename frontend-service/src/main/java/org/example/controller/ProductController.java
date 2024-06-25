package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.exception.NotFoundException;
import org.example.model.products.Product;
import org.example.service.orders.OrderService;
import org.example.service.products.ProductService;
import org.example.service.reviews.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;
    private final OrderService orderService;

    @GetMapping
    public String getAll(Model model) {
        var products = productService.getAll();
        model.addAttribute("products", products);
        return "products/products";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable Long id) {
        var product = productService.getById(id);
        if (product == null) {
            throw new NotFoundException(String.format("Product with id=%s not found", id));
        }
        model.addAttribute("product", product);
        model.addAttribute("orders", orderService.getAllByProduct(product));
        model.addAttribute("productAndRatingInfo", reviewService.getRatingInfoOfProduct(product));
        return "products/product";
    }

    @GetMapping("/new")
    public String createPage(Model model) {
        var product = new Product();
        model.addAttribute("product", product);
        return "products/new";
    }

    @PostMapping
    public String create(@ModelAttribute Product product) {
        var productCreated = productService.create(product);
        return "redirect:/products/" + productCreated.getId();
    }

    @GetMapping("/{id}/edit")
    public String updatePage(@PathVariable Long id, Model model) {
        var product = productService.getById(id);
        if (product == null) {
            throw new NotFoundException(String.format("Product with id=%s not found", id));
        }
        model.addAttribute("product", product);
        return "products/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Product product) {
        productService.update(id, product);
        return "redirect:/products/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }
}
