package org.example.repository;

import org.example.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findAllByProductIdOrderByRatingDesc(@Param("productId") Long productId);
    List<Review> findAllByProductIdInOrderByRatingDesc(@Param("productIds") List<Long> productIds);

    List<Review> findAllByUserIdOrderByRatingDesc(@Param("userId") Long userId);
    List<Review> findAllByUserIdInOrderByRatingDesc(@Param("userIds") List<Long> userIds);

    List<Review> findAllByRatingBetweenOrderByRatingDesc(@Param("ratingMin") Integer ratingMin, @Param("ratingMax") Integer ratingMax);
}
