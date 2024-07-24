package org.example.repository.reviews;

import lombok.extern.slf4j.Slf4j;
import org.example.model.products.Product;
import org.example.model.reviews.ProductAndRatingInfo;
import org.example.model.reviews.Review;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewRepositoryDummy implements ReviewRepository {

    private final List<Review> reviews = new ArrayList<>();

    @Override
    public Review getById(String id) {
        log.info("Searching for review with id={}", id);
        return reviews.stream()
                .filter(review -> Objects.equals(review.getId(), id))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<Review> getByIds(Set<String> ids) {
        log.info("Searching for reviews with ids={}", ids);
        return reviews.stream()
                .filter(review -> ids.contains(review.getId()))
                .toList();
    }

    @Override
    public List<Review> getAll() {
        log.info("Searching for all reviews");
        var reviewsResult = new ArrayList<>(reviews);
        Collections.sort(reviewsResult);
        return reviewsResult;
    }

    @Override
    public ProductAndRatingInfo getRatingInfoOfProduct(Long productId) {

        log.info("Searching for average rating for productId={}", productId);

        var reviews = getAllByProduct(productId);

        Product product;

        if (reviews.isEmpty()) {
            product = new Product();
            product.setId(productId);
        } else {
            var firstReview = reviews.getFirst();
            product = firstReview.getProduct();
        }

        return new ProductAndRatingInfo(product, reviews);
    }

    @Override
    public List<ProductAndRatingInfo> getRatingInfoOfProducts(List<Long> productIds) {
        log.info("Searching for average rating for productIds in {}", productIds);
        var reviews = getAllByProducts(productIds);
        return ProductAndRatingInfo.reviewsToProductAndRatings(reviews);
    }

    protected List<Review> getAllByProduct(Long productId) {
        log.info("Searching for reviews with productId={}", productId);
        return reviews.stream()
                .filter(review -> review.getProduct() != null)
                .filter(review -> Objects.equals(review.getProduct().getId(), productId))
                .toList();
    }

    protected List<Review> getAllByProducts(List<Long> productIds) {
        log.info("Searching for reviews with productId in {}", productIds);
        var productIdsSet = new HashSet<>(productIds);
        return reviews.stream()
                .filter(review -> review.getProduct() != null)
                .filter(review -> productIdsSet.contains(review.getProduct().getId()))
                .toList();
    }

    @Override
    public List<Review> getAllByUser(Long userId) {
        log.info("Searching for reviews with userId={}", userId);
        return reviews.stream()
                .filter(review -> review.getUser() != null)
                .filter(review -> Objects.equals(review.getUser().getId(), userId))
                .toList();
    }

    @Override
    public List<Review> getAllByUsers(List<Long> userIds) {
        log.info("Searching for reviews with userId in {}", userIds);
        var usersIdsSet = new HashSet<>(userIds);
        return reviews.stream()
                .filter(review -> review.getUser() != null)
                .filter(review -> usersIdsSet.contains(review.getUser().getId()))
                .toList();
    }

    @Override
    public List<Review> getAllByRatingBetween(Integer ratingMin, Integer ratingMax) {

        log.info("Searching for reviews by rating between {} - {}", ratingMin, ratingMax);

        var ratingMinFeatured = Objects.requireNonNullElse(ratingMin, Integer.MIN_VALUE);
        var ratingMaxFeatured = Objects.requireNonNullElse(ratingMax, Integer.MAX_VALUE);
        if (ratingMinFeatured > ratingMaxFeatured) {
            log.error("ratingMin ({}) is bigger than ratingMax ({})", ratingMin, ratingMax);
            return Collections.emptyList();
        }

        return reviews.stream()
                .filter(review -> review.getRating() != null)
                .filter(review -> review.getRating() >= ratingMinFeatured
                                && review.getRating() <= ratingMaxFeatured)
                .toList();
    }

    @Override
    public Review create(Review review) {
        log.info("Creating review '{}'", review);
        var nextId = nextId();
        review.setId(nextId);
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(LocalDateTime.now());
        }
        reviews.add(review);
        return review;
    }

    @Override
    public synchronized Review update(String id, Review review) {
        
        log.info("Updating order with id={}, {}", id, review);
        
        var reviewExisting = getById(id);
        if (reviewExisting == null) {
            log.error("Order with id {} not found", id);
            return null;
        }

        if (review.getUser() != null) {
            reviewExisting.setUser(review.getUser());
        }
        if (review.getProduct() != null) {
            reviewExisting.setProduct(review.getProduct());
        }
        if (review.getRating() != null) {
            reviewExisting.setRating(review.getRating());
        }
        if (review.getCreatedAt() != null) {
            reviewExisting.setCreatedAt(review.getCreatedAt());
        }
        
        return reviewExisting;
    }

    @Override
    public synchronized void deleteById(String id) {
        log.warn("Deleting review by id={}", id);
        var indexOfOrderInList = getIndexOfReviewInListById(id);
        if (indexOfOrderInList == -1) {
            return;
        }
        reviews.remove(indexOfOrderInList);
    }

    private String nextId() {

        var ids = reviews.stream()
                .map(Review::getId)
                .collect(Collectors.toSet());

        while (true) {
            var nextId = String.valueOf(new Random().nextLong());
            if (!ids.contains(nextId)) {
                return nextId;
            }
        }
    }

    private int getIndexOfReviewInListById(String id) {
        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}
