package org.example.service;

import net.devh.boot.grpc.server.serverfactory.GrpcServerLifecycle;
import org.example.model.ProductAndRatingInfo;
import org.example.model.Review;
import org.example.model.ReviewDTO;
import org.example.repository.ReviewRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private GrpcServerLifecycle grpcServerLifecycle; // To ignore bean creation

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnReviewWhenGetById() {

        var review = easyRandom.nextObject(Review.class);
        var id = review.getId();

        when(reviewRepository.findById(id)).thenReturn(Optional.of(review));

        var reviewReceived = reviewService.getById(id);
        assertNotNull(reviewReceived);
        assertEquals(review, reviewReceived);

        verify(reviewRepository, times(1)).findById(id);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = "99";

        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        var reviewReceived = reviewService.getById(id);
        assertNull(reviewReceived);

        verify(reviewRepository, times(1)).findById(id);
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAll() {

        var reviews = easyRandom.objects(Review.class, 7).toList();

        when(reviewRepository.findAll()).thenReturn(reviews);

        var reviewsReceived = reviewService.getAll();
        assertNotNull(reviewsReceived);
        assertEquals(reviews, reviewsReceived);

        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {

        when(reviewRepository.findAll()).thenReturn(Collections.emptyList());

        var reviewsReceived = reviewService.getAll();
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnProductAndRatingInfoWhenGetRatingInfoOfProduct() {

        var productId = 98L;

        var reviews = easyRandom.objects(Review.class, 7).toList();
        reviews.forEach(review -> review.setProductId(productId));

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        productAndRatingInfo.setProductId(productId);
        productAndRatingInfo.setReviews(reviews);

        when(reviewRepository.findAllByProductIdOrderByRatingDesc(productId)).thenReturn(reviews);

        var productAndRatingInfoReceived = reviewService.getRatingInfoOfProduct(productId);
        assertNotNull(productAndRatingInfoReceived);
        assertEquals(productAndRatingInfo, productAndRatingInfoReceived);
        assertEquals(productId, productAndRatingInfoReceived.getProductId());
        assertEquals(reviews, productAndRatingInfoReceived.getReviews());

        verify(reviewRepository, times(1)).findAllByProductIdOrderByRatingDesc(productId);
    }

    @Test
    void shouldReturnProductAndRatingInfoWithEmptyReviewsWhenGetRatingInfoOfProduct() {

        var productId = 99L;

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        productAndRatingInfo.setProductId(productId);
        productAndRatingInfo.setReviews(Collections.emptyList());

        when(reviewRepository.findAllByProductIdOrderByRatingDesc(productId)).thenReturn(Collections.emptyList());

        var productAndRatingInfoReceived = reviewService.getRatingInfoOfProduct(productId);
        assertNotNull(productAndRatingInfoReceived);
        assertEquals(productAndRatingInfo, productAndRatingInfoReceived);
        assertEquals(productId, productAndRatingInfoReceived.getProductId());
        assertTrue(productAndRatingInfoReceived.getReviews().isEmpty());

        verify(reviewRepository, times(1)).findAllByProductIdOrderByRatingDesc(productId);
    }

    @Test
    void shouldReturnListOfProductAndRatingInfoWhenGetRatingInfoOfProducts() {

        var productIdsAll = easyRandom.objects(Long.class, 10).toList();

        var reviewsAll = new ArrayList<Review>();

        for (var productId : productIdsAll) {

            var reviews = easyRandom.objects(Review.class, 5).toList();
            reviews.forEach(review -> review.setProductId(productId));

            reviewsAll.addAll(reviews);
        }

        Collections.shuffle(reviewsAll);

        var step = 2;

        for (int i = 0; i < productIdsAll.size()/2; i++) {

            var productIds = productIdsAll.subList(i, i + step);

            var reviews = reviewsAll.stream()
                    .filter(review -> productIds.contains(review.getProductId()))
                    .toList();

            when(reviewRepository.findAllByProductIdInOrderByRatingDesc(productIds)).thenReturn(reviews);

            var productAndRatingInfoListReceived = reviewService.getRatingInfoOfProducts(productIds);
            assertNotNull(productAndRatingInfoListReceived);

            for (var productAndRatingInfoReceived : productAndRatingInfoListReceived) {

                assertTrue(productIds.contains(productAndRatingInfoReceived.getProductId()));

                var reviewsByProduct = reviewsAll.stream()
                        .filter(review -> review.getProductId().equals(productAndRatingInfoReceived.getProductId()))
                        .collect(Collectors.toSet());

                assertEquals(reviewsByProduct, new HashSet<>(productAndRatingInfoReceived.getReviews()));
            }

            verify(reviewRepository, times(1)).findAllByProductIdInOrderByRatingDesc(productIds);
        }

        verify(reviewRepository, times(productIdsAll.size()/2)).findAllByProductIdInOrderByRatingDesc(any());
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByProduct() {

        var productId = 98L;

        var reviews = easyRandom.objects(Review.class, 7).toList();
        reviews.forEach(review -> review.setProductId(productId));

        when(reviewRepository.findAllByProductIdOrderByRatingDesc(productId)).thenReturn(reviews);

        var reviewsReceived = reviewService.getAllByProduct(productId);
        assertNotNull(reviewsReceived);
        assertEquals(reviews, reviewsReceived);

        verify(reviewRepository, times(1)).findAllByProductIdOrderByRatingDesc(productId);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByProduct() {

        var productId = 99L;

        when(reviewRepository.findAllByProductIdOrderByRatingDesc(productId)).thenReturn(Collections.emptyList());

        var reviewsReceived = reviewService.getAllByProduct(productId);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(reviewRepository, times(1)).findAllByProductIdOrderByRatingDesc(productId);
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByProducts() {

        var productIdsAll = easyRandom.objects(Long.class, 10).toList();

        var reviewsAll = new ArrayList<Review>();

        for (var productId : productIdsAll) {

            var reviews = easyRandom.objects(Review.class, 5).toList();
            reviews.forEach(review -> review.setProductId(productId));

            reviewsAll.addAll(reviews);
        }

        Collections.shuffle(reviewsAll);

        when(reviewRepository.findAllByProductIdInOrderByRatingDesc(productIdsAll)).thenReturn(reviewsAll);

        var reviewsReceived = reviewService.getAllByProducts(productIdsAll);
        assertNotNull(reviewsReceived);
        assertEquals(new HashSet<>(reviewsAll), new HashSet<>(reviewsReceived));

        verify(reviewRepository, times(1)).findAllByProductIdInOrderByRatingDesc(productIdsAll);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByProducts() {

        var productIdsAll = easyRandom.objects(Long.class, 10).toList();

        when(reviewRepository.findAllByProductIdInOrderByRatingDesc(productIdsAll)).thenReturn(Collections.emptyList());

        var reviewsReceived = reviewService.getAllByProducts(productIdsAll);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(reviewRepository, times(1)).findAllByProductIdInOrderByRatingDesc(productIdsAll);
    }


    @Test
    void shouldReturnListOfReviewsWhenGetAllByUser() {

        var userId = 98L;

        var reviews = easyRandom.objects(Review.class, 7).toList();
        reviews.forEach(review -> review.setUserId(userId));

        when(reviewRepository.findAllByUserIdOrderByRatingDesc(userId)).thenReturn(reviews);

        var reviewsReceived = reviewService.getAllByUser(userId);
        assertNotNull(reviewsReceived);
        assertEquals(reviews, reviewsReceived);

        verify(reviewRepository, times(1)).findAllByUserIdOrderByRatingDesc(userId);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUser() {

        var userId = 99L;

        when(reviewRepository.findAllByUserIdOrderByRatingDesc(userId)).thenReturn(Collections.emptyList());

        var reviewsReceived = reviewService.getAllByUser(userId);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(reviewRepository, times(1)).findAllByUserIdOrderByRatingDesc(userId);
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByUsers() {

        var userIdsAll = easyRandom.objects(Long.class, 10).toList();

        var reviewsAll = new ArrayList<Review>();

        for (var userId : userIdsAll) {

            var reviews = easyRandom.objects(Review.class, 5).toList();
            reviews.forEach(review -> review.setUserId(userId));

            reviewsAll.addAll(reviews);
        }

        Collections.shuffle(reviewsAll);

        when(reviewRepository.findAllByUserIdInOrderByRatingDesc(userIdsAll)).thenReturn(reviewsAll);

        var reviewsReceived = reviewService.getAllByUsers(userIdsAll);
        assertNotNull(reviewsReceived);
        assertEquals(new HashSet<>(reviewsAll), new HashSet<>(reviewsReceived));

        verify(reviewRepository, times(1)).findAllByUserIdInOrderByRatingDesc(userIdsAll);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUsers() {

        var userIdsAll = easyRandom.objects(Long.class, 10).toList();

        when(reviewRepository.findAllByUserIdInOrderByRatingDesc(userIdsAll)).thenReturn(Collections.emptyList());

        var reviewsReceived = reviewService.getAllByUsers(userIdsAll);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(reviewRepository, times(1)).findAllByUserIdInOrderByRatingDesc(userIdsAll);
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByRatingBetween() {

        var reviews = easyRandom.objects(Review.class, 10).toList();

        var ratingMin = 3;
        var ratingMax = 7;

        when(reviewRepository.findAllByRatingBetweenOrderByRatingDesc(ratingMin, ratingMax)).thenReturn(reviews);

        var reviewsReceived = reviewService.getAllByRatingBetween(ratingMin, ratingMax);
        assertNotNull(reviewsReceived);
        assertEquals(reviews, reviewsReceived);

        verify(reviewRepository, times(1)).findAllByRatingBetweenOrderByRatingDesc(ratingMin, ratingMax);
    }

    @Test
    void shouldReturnListOfReviewsIfRatingMinNullWhenGetAllByRatingBetween() {

        var reviews = easyRandom.objects(Review.class, 10).toList();

        var ratingMax = 10;

        when(reviewRepository.findAllByRatingBetweenOrderByRatingDesc(any(), any())).thenReturn(reviews);

        var reviewsReceived = reviewService.getAllByRatingBetween(null, ratingMax);
        assertNotNull(reviewsReceived);
        assertEquals(reviews, reviewsReceived);

        verify(reviewRepository, times(1)).findAllByRatingBetweenOrderByRatingDesc(Integer.MIN_VALUE, ratingMax);
    }

    @Test
    void shouldReturnListOfReviewsIfRatingMaxNullWhenGetAllByRatingBetween() {

        var reviews = easyRandom.objects(Review.class, 10).toList();

        var ratingMin = 10;

        when(reviewRepository.findAllByRatingBetweenOrderByRatingDesc(any(), any())).thenReturn(reviews);

        var reviewsReceived = reviewService.getAllByRatingBetween(ratingMin, null);
        assertNotNull(reviewsReceived);
        assertEquals(reviews, reviewsReceived);

        verify(reviewRepository, times(1)).findAllByRatingBetweenOrderByRatingDesc(ratingMin, Integer.MAX_VALUE);
    }

    @Test
    void shouldReturnEmptyListIfRatingMinBiggerThanRatingMaxWhenGetAllByRatingBetween() {

        var ratingMin = 2;
        var ratingMax = 1;

        var reviewsReceived = reviewService.getAllByRatingBetween(ratingMin, ratingMax);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(reviewRepository, never()).findAllByRatingBetweenOrderByRatingDesc(any(), any());
    }

    @Test
    void shouldReturnReviewWhenCreate() {

        var reviewDTO = easyRandom.nextObject(ReviewDTO.class);
        var newId = "123";

        when(reviewRepository.save(any(Review.class))).thenAnswer(ans -> {
            var reviewParam = ans.getArgument(0, Review.class);
            reviewParam.setId(newId);
            return reviewParam;
        });

        var reviewCreated = reviewService.create(reviewDTO);
        assertNotNull(reviewCreated);
        assertNotNull(reviewCreated.getId());
        assertEquals(newId, reviewCreated.getId());
        assertEquals(reviewDTO.toReview(), reviewCreated);

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void shouldReturnReviewWhenUpdate() {

        var reviewExisting = easyRandom.nextObject(Review.class);
        var id = reviewExisting.getId();

        when(reviewRepository.findById(id)).thenReturn(Optional.of(reviewExisting));
        when(reviewRepository.save(any(Review.class))).thenAnswer(ans -> ans.getArgument(0, Review.class));

        var reviewDTO = easyRandom.nextObject(ReviewDTO.class);

        var reviewUpdated = reviewService.update(id, reviewDTO);
        assertNotNull(reviewUpdated);
        assertEquals(id, reviewUpdated.getId());
        assertEquals(reviewDTO.getProductId(), reviewUpdated.getProductId());
        assertEquals(reviewDTO.getUserId(), reviewUpdated.getUserId());
        assertEquals(reviewDTO.getRating(), reviewUpdated.getRating());
        assertEquals(reviewDTO.getCreatedAt(), reviewUpdated.getCreatedAt());

        verify(reviewRepository, times(1)).findById(id);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var id = "1";

        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        var reviewDTO = easyRandom.nextObject(ReviewDTO.class);

        var reviewUpdated = reviewService.update(id, reviewDTO);
        assertNull(reviewUpdated);

        verify(reviewRepository, times(1)).findById(id);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldReturnReviewWhenDeleteById() {
        var id = "987";
        reviewService.deleteById(id);
        verify(reviewRepository, times(1)).deleteById(id);
    }
}