package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.exception.NotFoundException;
import org.example.model.products.Product;
import org.example.model.reviews.Review;
import org.example.service.reviews.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public String getAll(Model model) {
        var reviews = reviewService.getAll();
        model.addAttribute("reviews", reviews);
        return "reviews/reviews";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable String id) {
        var review = reviewService.getById(id);
        if (review == null) {
            throw new NotFoundException(String.format("Review with id=%s not found", id));
        }
        model.addAttribute("review", review);
        return "reviews/review";
    }

    @GetMapping("/new")
    public String createPage(Model model, @RequestParam(required = false) Product product) {
        var review = new Review();
        if (product != null) {
            review.setProduct(product);
        }
        model.addAttribute("review", review);
        return "reviews/new";
    }

    @PostMapping
    public String create(@ModelAttribute Review review) {
        var reviewCreated = reviewService.create(review);
        return "redirect:/reviews/" + reviewCreated.getId();
    }

    @GetMapping("/{id}/edit")
    public String updatePage(@PathVariable String id, Model model) {
        var review = reviewService.getById(id);
        if (review == null) {
            throw new NotFoundException(String.format("Review with id=%s not found", id));
        }
        model.addAttribute("review", review);
        return "reviews/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable String id, @ModelAttribute Review review) {
        reviewService.update(id, review);
        return "redirect:/reviews/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable String id) {
        reviewService.deleteById(id);
        return "redirect:/reviews";
    }
}
