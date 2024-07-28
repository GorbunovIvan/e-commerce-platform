package org.example.repository.reviews;

import org.example.model.reviews.ProductAndRatingInfo;
import org.example.model.reviews.Review;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface ReviewRepository {
    Review getById(String id);
    List<Review> getByIds(Set<String> ids);
    List<Review> getAll();
    ProductAndRatingInfo getRatingInfoOfProduct(Long productId);
    List<ProductAndRatingInfo> getRatingInfoOfProducts(List<Long> productIds);
    List<Review> getAllByUser(Long userId);
    List<Review> getAllByUsers(List<Long> userIds);
    List<Review> getAllByRatingBetween(Integer ratingMin, Integer ratingMax);
    Review create(Review review);
    Review update(String id, Review review);
    void deleteById(String id);
}
