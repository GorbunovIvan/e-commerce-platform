package org.example.repository;

import net.devh.boot.grpc.server.serverfactory.GrpcServerLifecycle;
import org.example.model.Review;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @MockBean
    private GrpcServerLifecycle grpcServerLifecycle; // To ignore bean creation

    private final EasyRandom easyRandom = new EasyRandom();

    @AfterEach
    void tearDown() {
        // Be careful, make sure this is definitely a test mongo database.
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
    }

    @Test
    void shouldReturnListOfReviewsWhenFindAllByProductIdOrderByRatingDesc() {

        var reviewsAll = easyRandom.objects(Review.class, 10).toList();
        reviewsAll.forEach(review -> review.setId(null));

        var reviewsInMongoDB = mongoTemplate.insertAll(reviewsAll);

        var mapProductIdAndReviews = reviewsInMongoDB.stream()
                .collect(Collectors.groupingBy(Review::getProductId));

        for (var entry : mapProductIdAndReviews.entrySet()) {

            var productId = entry.getKey();
            var reviews = entry.getValue();

            reviews.sort(Comparator.comparing(Review::getRating).reversed());

            var reviewsReceived = reviewRepository.findAllByProductIdOrderByRatingDesc(productId);
            assertNotNull(reviewsReceived);
            assertEquals(reviews, reviewsReceived);
        }
    }

    @Test
    void shouldReturnListOfReviewsBySingleProductIdWhenFindAllByProductIdOrderByRatingDesc() {

        var productId = 99L;

        var reviewsAll = easyRandom.objects(Review.class, 10).toList();
        reviewsAll.forEach(review -> review.setId(null));
        reviewsAll.forEach(review -> review.setProductId(productId));

        var reviewsInMongoDB = mongoTemplate.insertAll(reviewsAll);

        var reviewsExpected = new ArrayList<>(reviewsInMongoDB);
        reviewsExpected.sort(Comparator.comparing(Review::getRating).reversed());

        var reviewsReceived = reviewRepository.findAllByProductIdOrderByRatingDesc(productId);
        assertNotNull(reviewsReceived);
        assertEquals(reviewsExpected, reviewsReceived);
    }

    @Test
    void shouldReturnListOfReviewsWhenFindAllByProductIdInOrderByRatingDesc() {

        var productIdsAll = easyRandom.objects(Long.class, 10).toList();

        var reviewsAll = new ArrayList<Review>();

        for (var productId : productIdsAll) {

            var reviews = easyRandom.objects(Review.class, 5).toList();
            reviews.forEach(review -> review.setId(null));
            reviews.forEach(review -> review.setProductId(productId));

            reviewsAll.addAll(reviews);
        }

        Collections.shuffle(reviewsAll);

        var reviewsInMongoDB = mongoTemplate.insertAll(reviewsAll);

        var step = 2;

        for (int i = 0; i < productIdsAll.size()/2; i++) {

            var productIds = productIdsAll.subList(i, i + step);

            var reviews = reviewsInMongoDB.stream()
                    .filter(review -> productIds.contains(review.getProductId()))
                    .sorted(Comparator.comparing(Review::getRating).reversed())
                    .toList();

            var reviewsReceived = reviewRepository.findAllByProductIdInOrderByRatingDesc(productIds);
            assertNotNull(reviewsReceived);
            assertEquals(reviews, reviewsReceived);
        }
    }

    @Test
    void shouldReturnListOfReviewsWhenFindAllByUserIdOrderByRatingDesc() {

        var reviewsAll = easyRandom.objects(Review.class, 10).toList();
        reviewsAll.forEach(review -> review.setId(null));

        var reviewsInMongoDB = mongoTemplate.insertAll(reviewsAll);

        var mapUserIdAndReviews = reviewsInMongoDB.stream()
                .collect(Collectors.groupingBy(Review::getUserId));

        for (var entry : mapUserIdAndReviews.entrySet()) {

            var userId = entry.getKey();
            var reviews = entry.getValue();

            reviews.sort(Comparator.comparing(Review::getRating).reversed());

            var reviewsReceived = reviewRepository.findAllByUserIdOrderByRatingDesc(userId);
            assertNotNull(reviewsReceived);
            assertEquals(reviews, reviewsReceived);
        }
    }

    @Test
    void shouldReturnListOfReviewsBySingleUserIdWhenFindAllByUserIdOrderByRatingDesc() {

        var userId = 99L;

        var reviewsAll = easyRandom.objects(Review.class, 10).toList();
        reviewsAll.forEach(review -> review.setId(null));
        reviewsAll.forEach(review -> review.setUserId(userId));

        var reviewsInMongoDB = mongoTemplate.insertAll(reviewsAll);

        var reviewsExpected = new ArrayList<>(reviewsInMongoDB);
        reviewsExpected.sort(Comparator.comparing(Review::getRating).reversed());

        var reviewsReceived = reviewRepository.findAllByUserIdOrderByRatingDesc(userId);
        assertNotNull(reviewsReceived);
        assertEquals(reviewsExpected, reviewsReceived);
    }

    @Test
    void shouldReturnListOfReviewsWhenFindAllByUserIdInOrderByRatingDesc() {

        var userIdsAll = easyRandom.objects(Long.class, 10).toList();

        var reviewsAll = new ArrayList<Review>();

        for (var userId : userIdsAll) {

            var reviews = easyRandom.objects(Review.class, 5).toList();
            reviews.forEach(review -> review.setId(null));
            reviews.forEach(review -> review.setUserId(userId));

            reviewsAll.addAll(reviews);
        }

        Collections.shuffle(reviewsAll);

        var reviewsInMongoDB = mongoTemplate.insertAll(reviewsAll);

        var step = 2;

        for (int i = 0; i < userIdsAll.size()/2; i+=step) {

            var userIds = userIdsAll.subList(i, i + step);

            var reviews = reviewsInMongoDB.stream()
                    .filter(review -> userIds.contains(review.getUserId()))
                    .sorted(Comparator.comparing(Review::getRating).reversed())
                    .toList();

            var reviewsReceived = reviewRepository.findAllByUserIdInOrderByRatingDesc(userIds);
            assertNotNull(reviewsReceived);
            assertEquals(reviews, reviewsReceived);
        }
    }

    @Test
    void shouldReturnListOfReviewsWhenFindAllByRatingBetweenOrderByRatingDesc() {

        var reviewsAll = easyRandom.objects(Review.class, 10).toList();
        reviewsAll.forEach(review -> review.setId(null));

        var reviewsInMongoDB = mongoTemplate.insertAll(reviewsAll);

        for (int i = 0; i < 100; i++) {

            var ratingMin = easyRandom.nextInt(0, 10);
            var ratingMax = easyRandom.nextInt(ratingMin, 10);

            var reviewsExpected = reviewsInMongoDB.stream()
                    .filter(review -> review.getRating() >= ratingMin
                                    && review.getRating() <= ratingMax)
                    .sorted(Comparator.comparing(Review::getRating).reversed())
                    .toList();

            var reviewsReceived = reviewRepository.findAllByRatingBetweenOrderByRatingDesc(ratingMin, ratingMax);
            assertNotNull(reviewsReceived);
            assertEquals(reviewsExpected, reviewsReceived);
        }
    }
}