package org.example.service.reviews;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.reviews.ProductAndRatingInfo;
import org.example.model.reviews.Review;
import org.example.repository.reviews.ReviewRepositoryDummy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepositoryDummy reviewRepository;

    public Review getById(String id) {
        log.info("Searching for review with id={}", id);
        return reviewRepository.getById(id);
    }

    public List<Review> getAll() {
        log.info("Searching for all reviews");
        return reviewRepository.getAll();
    }

    public ProductAndRatingInfo getRatingInfoOfProduct(Long productId) {
        log.info("Searching for average rating for productId={}", productId);
        return reviewRepository.getRatingInfoOfProduct(productId);
    }

    public List<ProductAndRatingInfo> getRatingInfoOfProducts(List<Long> productIds) {
        log.info("Searching for average rating for productIds in {}", productIds);
        return reviewRepository.getRatingInfoOfProducts(productIds);
    }

    public List<Review> getAllByUser(Long userId) {
        log.info("Searching for reviews with userId={}", userId);
        return reviewRepository.getAllByUser(userId);
    }

    public List<Review> getAllByUsers(List<Long> userIds) {
        log.info("Searching for reviews with userId in {}", userIds);
        return reviewRepository.getAllByUsers(userIds);
    }

    public List<Review> getAllByRatingBetween(Integer ratingMin, Integer ratingMax) {
        log.info("Searching for reviews by rating between {} - {}", ratingMin, ratingMax);
        return reviewRepository.getAllByRatingBetween(ratingMin, ratingMax);
    }

    public Review create(Review review) {
        log.info("Creating review '{}'", review);
        return reviewRepository.create(review);
    }

    public Review update(String id, Review review) {
        log.info("Updating review with id={}, {}", id, review);
        return reviewRepository.update(id, review);
    }

    public void deleteById(String id) {
        log.warn("Deleting review by id={}", id);
        reviewRepository.deleteById(id);
    }
}
