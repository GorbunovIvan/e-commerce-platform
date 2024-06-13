package org.example.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.ProductAndRatingInfo;
import org.example.model.Review;
import org.example.model.ReviewDTO;
import org.example.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review getById(String id) {
        log.info("Searching for review with id={}", id);
        var reviewOptional = reviewRepository.findById(id);
        return reviewOptional.orElse(null);
    }

    public List<Review> getAll() {
        log.info("Searching for all reviews");
        return reviewRepository.findAll();
    }

    public ProductAndRatingInfo getRatingInfoOfProduct(Long productId) {
        log.info("Searching for average rating for productId={}", productId);
        var reviews = getAllByProduct(productId);
        return new ProductAndRatingInfo(productId, reviews);
    }

    public List<ProductAndRatingInfo> getRatingInfoOfProducts(List<Long> productIds) {
        log.info("Searching for average rating for productIds in {}", productIds);
        var reviews = getAllByProducts(productIds);
        return ProductAndRatingInfo.reviewsToProductAndRatings(reviews);
    }

    protected List<Review> getAllByProduct(Long productId) {
        log.info("Searching for reviews with productId={}", productId);
        return reviewRepository.findAllByProductIdOrderByRatingDesc(productId);
    }

    protected List<Review> getAllByProducts(List<Long> productIds) {
        log.info("Searching for reviews with productId in {}", productIds);
        return reviewRepository.findAllByProductIdInOrderByRatingDesc(productIds);
    }

    public List<Review> getAllByUser(Long userId) {
        log.info("Searching for reviews with userId={}", userId);
        return reviewRepository.findAllByUserIdOrderByRatingDesc(userId);
    }

    public List<Review> getAllByUsers(List<Long> userIds) {
        log.info("Searching for reviews with userId in {}", userIds);
        return reviewRepository.findAllByUserIdInOrderByRatingDesc(userIds);
    }

    public List<Review> getAllByRatingBetween(Integer ratingMin, Integer ratingMax) {
        log.info("Searching for reviews by rating between {} - {}", ratingMin, ratingMax);
        ratingMin = Objects.requireNonNullElse(ratingMin, Integer.MIN_VALUE);
        ratingMax = Objects.requireNonNullElse(ratingMax, Integer.MAX_VALUE);
        return reviewRepository.findAllByRatingBetweenOrderByRatingDesc(ratingMin, ratingMax);
    }

    public Review create(@NotNull ReviewDTO reviewDTO) {
        log.info("Creating review '{}'", reviewDTO);
        var review = reviewDTO.toReview();
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(LocalDateTime.now());
        }
        return reviewRepository.save(review);
    }

    @Transactional
    public Review update(String id, @NotNull ReviewDTO reviewDTO) {

        log.info("Updating review with id={}, {}", id, reviewDTO);

        var reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isEmpty()) {
            log.error("Review with id {} not found", id);
            return null;
        }

        var review = reviewOptional.get();

        if (reviewDTO.getUserId() != null) {
            review.setUserId(reviewDTO.getUserId());
        }
        if (reviewDTO.getProductId() != null) {
            review.setProductId(reviewDTO.getProductId());
        }
        if (reviewDTO.getRating() != null) {
            review.setRating(reviewDTO.getRating());
        }
        if (reviewDTO.getCreatedAt() != null) {
            review.setCreatedAt(reviewDTO.getCreatedAt());
        }

        return reviewRepository.save(review);
    }

    public void deleteById(String id) {
        log.warn("Deleting review by id={}", id);
        reviewRepository.deleteById(id);
    }
}
