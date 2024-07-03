package org.example.service.reviews;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.products.Product;
import org.example.model.reviews.ProductAndRatingInfo;
import org.example.model.reviews.Review;
import org.example.model.users.User;
import org.example.repository.reviews.ReviewRepository;
import org.example.service.ModelBinder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ModelBinder modelBinder;

    public Review getById(String id) {
        log.info("Searching for review with id={}", id);
        var result = reviewRepository.getById(id);
        return modelBinder.bindFields(result);
    }

    public List<Review> getAll() {
        log.info("Searching for all reviews");
        var result = reviewRepository.getAll();
        return modelBinder.bindFields(result);
    }

    public ProductAndRatingInfo getRatingInfoOfProduct(Product product) {
        if (product == null) {
            return new ProductAndRatingInfo();
        }
        return getRatingInfoOfProduct(product.getId());
    }

    public ProductAndRatingInfo getRatingInfoOfProduct(Long productId) {
        log.info("Searching for average rating for productId={}", productId);
        var result = reviewRepository.getRatingInfoOfProduct(productId);
        return modelBinder.bindFields(result);
    }

    public List<ProductAndRatingInfo> getRatingInfoOfProducts(List<Long> productIds) {
        log.info("Searching for average rating for productIds in {}", productIds);
        var result = reviewRepository.getRatingInfoOfProducts(productIds);
        return modelBinder.bindFields(result);
    }

    public List<Review> getAllByUser(User user) {
        if (user == null) {
            return Collections.emptyList();
        }
        return this.getAllByUser(user.getId());
    }

    public List<Review> getAllByUser(Long userId) {
        log.info("Searching for reviews with userId={}", userId);
        var result = reviewRepository.getAllByUser(userId);
        return modelBinder.bindFields(result);
    }

    public List<Review> getAllByUsers(List<Long> userIds) {
        log.info("Searching for reviews with userId in {}", userIds);
        var result = reviewRepository.getAllByUsers(userIds);
        return modelBinder.bindFields(result);
    }

    public List<Review> getAllByRatingBetween(Integer ratingMin, Integer ratingMax) {
        log.info("Searching for reviews by rating between {} - {}", ratingMin, ratingMax);
        var result = reviewRepository.getAllByRatingBetween(ratingMin, ratingMax);
        return modelBinder.bindFields(result);
    }

    public Review create(Review review) {
        log.info("Creating review '{}'", review);
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(LocalDateTime.now());
        }
        var result = reviewRepository.create(review);
        return modelBinder.bindFields(result);
    }

    public Review update(String id, Review review) {
        log.info("Updating review with id={}, {}", id, review);
        var result = reviewRepository.update(id, review);
        return modelBinder.bindFields(result);
    }

    public void deleteById(String id) {
        log.warn("Deleting review by id={}", id);
        reviewRepository.deleteById(id);
    }
}
