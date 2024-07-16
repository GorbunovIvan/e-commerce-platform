package org.example.repository.reviews.remote;

import com.google.protobuf.Empty;
import org.example.grpc.GrpcReviewServiceGrpc;
import org.example.grpc.GrpcReviewServiceOuterClass.*;
import org.example.model.products.Product;
import org.example.model.reviews.ProductAndRatingInfo;
import org.example.model.reviews.Review;
import org.example.model.users.User;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = "review-service.enabled=true")
class ReviewRepositoryGRPCTest {

    @Autowired
    private ReviewRepositoryGRPC reviewRepositoryGRPC;

    @MockBean
    private GrpcReviewServiceGrpc.GrpcReviewServiceBlockingStub blockingStub;

    @Autowired
    private ReviewsUtil reviewsUtil;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnReviewWhenGetById() {

        var review = easyRandom.nextObject(Review.class);
        var id = review.getId();

        var reviewResponse = reviewsUtil.toReviewResponse(review);

        when(blockingStub.getById(any(IdRequest.class))).thenReturn(reviewResponse);

        var reviewReceived = reviewRepositoryGRPC.getById(id);
        assertFalse(isReviewEmpty(reviewReceived));
        assertEquals(review.getId(), reviewReceived.getId());
        assertEquals(reviewResponse, reviewsUtil.toReviewResponse(reviewReceived));

        verify(blockingStub, times(1)).getById(any(IdRequest.class));
    }

    @Test
    void shouldReturnEmptyReviewWhenGetById() {

        var id = "99";

        var reviewResponse = ReviewResponse.newBuilder().build();

        when(blockingStub.getById(any(IdRequest.class))).thenReturn(reviewResponse);

        var reviewReceived = reviewRepositoryGRPC.getById(id);
        assertTrue(isReviewEmpty(reviewReceived));

        verify(blockingStub, times(1)).getById(any(IdRequest.class));
    }

    @Test
    void shouldReturnReviewWhenGetByIds() {

        var reviews = easyRandom.objects(Review.class, 5).toList();
        var ids = reviews.stream().map(Review::getId).collect(Collectors.toSet());

        var reviewsResponse = reviewsUtil.toReviewsResponse(reviews);

        when(blockingStub.getByIds(any(IdsRequest.class))).thenReturn(reviewsResponse);

        var reviewsReceived = reviewRepositoryGRPC.getByIds(ids);
        assertFalse(reviewsReceived.isEmpty());
        assertEquals(reviewsResponse, reviewsUtil.toReviewsResponse(reviewsReceived));

        verify(blockingStub, times(1)).getByIds(any(IdsRequest.class));
    }
    @Test
    void shouldReturnEmptyListWhenGetByIds() {

        var ids = easyRandom.objects(String.class, 4).collect(Collectors.toSet());

        var reviewsResponse = reviewsUtil.toReviewsResponse(Collections.emptyList());

        when(blockingStub.getByIds(any(IdsRequest.class))).thenReturn(reviewsResponse);

        var reviewsReceived = reviewRepositoryGRPC.getByIds(ids);
        assertTrue(reviewsReceived.isEmpty());
        assertEquals(reviewsResponse, reviewsUtil.toReviewsResponse(reviewsReceived));

        verify(blockingStub, times(1)).getByIds(any(IdsRequest.class));
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAll() {

        var reviews = easyRandom.objects(Review.class, 4).toList();
        var reviewsResponses = reviewsUtil.toReviewsResponse(reviews);

        when(blockingStub.getAll(Empty.newBuilder().build())).thenReturn(reviewsResponses);

        var reviewsReceived = reviewRepositoryGRPC.getAll();
        assertNotNull(reviewsReceived);
        assertFalse(reviewsReceived.isEmpty());
        assertEquals(reviewsResponses, reviewsUtil.toReviewsResponse(reviewsReceived));

        verify(blockingStub, times(1)).getAll(Empty.newBuilder().build());
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {

        var reviewsResponses = reviewsUtil.toReviewsResponse(Collections.emptyList());

        when(blockingStub.getAll(Empty.newBuilder().build())).thenReturn(reviewsResponses);

        var reviewsReceived = reviewRepositoryGRPC.getAll();
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(blockingStub, times(1)).getAll(Empty.newBuilder().build());
    }

    @Test
    void shouldReturnProductAndRatingInfoWhenGetRatingInfoOfProduct() {

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        var productAndRatingInfoResponse = reviewsUtil.toProductAndRatingInfoResponse(productAndRatingInfo);

        when(blockingStub.getRatingInfoOfProduct(any(IdNumberRequest.class))).thenReturn(productAndRatingInfoResponse);

        var productAndRatingInfoReceived = reviewRepositoryGRPC.getRatingInfoOfProduct(productAndRatingInfo.getProductId());
        assertNotNull(productAndRatingInfoReceived);
        assertEquals(productAndRatingInfo.getProductId(), productAndRatingInfoReceived.getProductId());
        assertEquals(reviewsUtil.toProductAndRatingInfoResponse(productAndRatingInfo), productAndRatingInfoResponse);

        verify(blockingStub, times(1)).getRatingInfoOfProduct(any(IdNumberRequest.class));
    }

    @Test
    void shouldReturnProductAndRatingInfoWithEmptyReviewsWhenGetRatingInfoOfProduct() {

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        productAndRatingInfo.setReviews(Collections.emptyList());

        var productAndRatingInfoResponse = reviewsUtil.toProductAndRatingInfoResponse(productAndRatingInfo);

        when(blockingStub.getRatingInfoOfProduct(any(IdNumberRequest.class))).thenReturn(productAndRatingInfoResponse);

        var productAndRatingInfoReceived = reviewRepositoryGRPC.getRatingInfoOfProduct(productAndRatingInfo.getProductId());
        assertNotNull(productAndRatingInfoReceived);
        assertTrue(productAndRatingInfoReceived.getReviews().isEmpty());

        verify(blockingStub, times(1)).getRatingInfoOfProduct(any(IdNumberRequest.class));
    }

    @Test
    void shouldReturnListOfProductAndRatingInfoWhenGetRatingInfoOfProducts() {

        var productAndRatingInfoList = easyRandom.objects(ProductAndRatingInfo.class, 4).toList();
        for (var productInfo : productAndRatingInfoList) {
            var reviews = easyRandom.objects(Review.class, 4).toList();
            productInfo.setReviews(reviews);
        }

        var productAndRatingInfoListResponse = reviewsUtil.toProductAndRatingInfoListResponse(productAndRatingInfoList);

        var ids = productAndRatingInfoList.stream().map(ProductAndRatingInfo::getProductId).toList();

        when(blockingStub.getRatingInfoOfProducts(any(IdNumbersRequest.class))).thenReturn(productAndRatingInfoListResponse);

        var productAndRatingInfoReceived = reviewRepositoryGRPC.getRatingInfoOfProducts(ids);
        assertEquals(productAndRatingInfoListResponse, reviewsUtil.toProductAndRatingInfoListResponse(productAndRatingInfoReceived));

        verify(blockingStub, times(1)).getRatingInfoOfProducts(any(IdNumbersRequest.class));
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByUser() {

        var user = easyRandom.nextObject(User.class);

        var reviews = easyRandom.objects(Review.class, 7).toList();
        reviews.forEach(review -> review.setUser(user));

        var reviewsResponse = reviewsUtil.toReviewsResponse(reviews);

        when(blockingStub.getAllByUser(any(IdNumberRequest.class))).thenReturn(reviewsResponse);

        var reviewsReceived = reviewRepositoryGRPC.getAllByUser(user.getId());
        assertNotNull(reviewsReceived);
        assertFalse(reviewsReceived.isEmpty());
        assertEquals(reviewsResponse, reviewsUtil.toReviewsResponse(reviewsReceived));

        verify(blockingStub, times(1)).getAllByUser(any(IdNumberRequest.class));
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUser() {

        var userId = 99L;

        var reviewsResponse = reviewsUtil.toReviewsResponse(Collections.emptyList());

        when(blockingStub.getAllByUser(any(IdNumberRequest.class))).thenReturn(reviewsResponse);

        var reviewsReceived = reviewRepositoryGRPC.getAllByUser(userId);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(blockingStub, times(1)).getAllByUser(any(IdNumberRequest.class));
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByUsers() {

        var reviews = easyRandom.objects(Review.class, 7).toList();
        var userIds = reviews.stream().map(Review::getUserId).toList();

        var reviewsResponse = reviewsUtil.toReviewsResponse(reviews);

        when(blockingStub.getAllByUsers(any(IdNumbersRequest.class))).thenReturn(reviewsResponse);

        var reviewsReceived = reviewRepositoryGRPC.getAllByUsers(userIds);
        assertNotNull(reviewsReceived);
        assertFalse(reviewsReceived.isEmpty());
        assertEquals(reviewsResponse, reviewsUtil.toReviewsResponse(reviewsReceived));

        verify(blockingStub, times(1)).getAllByUsers(any(IdNumbersRequest.class));
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUsers() {

        var userIds = easyRandom.objects(Long.class, 4).toList();

        var reviewsResponse = reviewsUtil.toReviewsResponse(Collections.emptyList());

        when(blockingStub.getAllByUsers(any(IdNumbersRequest.class))).thenReturn(reviewsResponse);

        var reviewsReceived = reviewRepositoryGRPC.getAllByUsers(userIds);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(blockingStub, times(1)).getAllByUsers(any(IdNumbersRequest.class));
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByRatingBetween() {

        var reviews = easyRandom.objects(Review.class, 10).toList();
        var reviewsResponse = reviewsUtil.toReviewsResponse(reviews);

        var ratingMin = 3;
        var ratingMax = 7;

        when(blockingStub.getAllByRatingBetween(any(RatingRangeRequest.class))).thenReturn(reviewsResponse);

        var reviewsReceived = reviewRepositoryGRPC.getAllByRatingBetween(ratingMin, ratingMax);
        assertNotNull(reviewsReceived);
        assertEquals(reviewsResponse, reviewsUtil.toReviewsResponse(reviewsReceived));

        var ratingRangeRequestCaptor = ArgumentCaptor.forClass(RatingRangeRequest.class);
        verify(blockingStub, times(1)).getAllByRatingBetween(ratingRangeRequestCaptor.capture());

        var ratingRequest = ratingRangeRequestCaptor.getValue();
        assertNotNull(ratingRequest);
        assertEquals(ratingMin, ratingRequest.getRatingMin());
        assertEquals(ratingMax, ratingRequest.getRatingMax());
    }

    @Test
    void shouldReturnListOfReviewsIfRatingMinNullWhenGetAllByRatingBetween() {

        var reviews = easyRandom.objects(Review.class, 10).toList();
        var reviewsResponse = reviewsUtil.toReviewsResponse(reviews);

        var ratingMax = 10;

        when(blockingStub.getAllByRatingBetween(any(RatingRangeRequest.class))).thenReturn(reviewsResponse);

        var reviewsReceived = reviewRepositoryGRPC.getAllByRatingBetween(null, ratingMax);
        assertNotNull(reviewsReceived);
        assertEquals(reviewsResponse, reviewsUtil.toReviewsResponse(reviewsReceived));

        var ratingRangeRequestCaptor = ArgumentCaptor.forClass(RatingRangeRequest.class);
        verify(blockingStub, times(1)).getAllByRatingBetween(ratingRangeRequestCaptor.capture());

        var ratingRequest = ratingRangeRequestCaptor.getValue();
        assertNotNull(ratingRequest);
        assertEquals(Integer.MIN_VALUE, ratingRequest.getRatingMin());
        assertEquals(ratingMax, ratingRequest.getRatingMax());
    }

    @Test
    void shouldReturnListOfReviewsIfRatingMaxNullWhenGetAllByRatingBetween() {

        var reviews = easyRandom.objects(Review.class, 10).toList();
        var reviewsResponse = reviewsUtil.toReviewsResponse(reviews);

        var ratingMin = 2;

        when(blockingStub.getAllByRatingBetween(any(RatingRangeRequest.class))).thenReturn(reviewsResponse);

        var reviewsReceived = reviewRepositoryGRPC.getAllByRatingBetween(ratingMin, null);
        assertNotNull(reviewsReceived);
        assertEquals(reviewsResponse, reviewsUtil.toReviewsResponse(reviewsReceived));

        var ratingRangeRequestCaptor = ArgumentCaptor.forClass(RatingRangeRequest.class);
        verify(blockingStub, times(1)).getAllByRatingBetween(ratingRangeRequestCaptor.capture());

        var ratingRequest = ratingRangeRequestCaptor.getValue();
        assertNotNull(ratingRequest);
        assertEquals(ratingMin, ratingRequest.getRatingMin());
        assertEquals(Integer.MAX_VALUE, ratingRequest.getRatingMax());
    }

    @Test
    void shouldReturnEmptyListIfRatingMinBiggerThanRatingMaxWhenGetAllByRatingBetween() {

        var ratingMin = 2;
        var ratingMax = 1;

        var reviewsReceived = reviewRepositoryGRPC.getAllByRatingBetween(ratingMin, ratingMax);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(blockingStub, never()).getAllByRatingBetween(any(RatingRangeRequest.class));
    }

    @Test
    void shouldReturnReviewWhenCreate() {

        var review = easyRandom.nextObject(Review.class);
        var reviewResponse = reviewsUtil.toReviewResponse(review);

        when(blockingStub.create(any(ReviewRequest.class))).thenReturn(reviewResponse);

        var reviewCreated = reviewRepositoryGRPC.create(review);
        assertNotNull(reviewCreated);
        assertNotNull(reviewCreated.getId());
        assertEquals(reviewResponse, reviewsUtil.toReviewResponse(reviewCreated));

        verify(blockingStub, times(1)).create(any(ReviewRequest.class));
    }

    @Test
    void shouldReturnReviewWhenUpdate() {

        var review = easyRandom.nextObject(Review.class);
        var id = review.getId();

        var reviewResponse = reviewsUtil.toReviewResponse(review);

        when(blockingStub.update(any(UpdateReviewRequest.class))).thenReturn(reviewResponse);

        var reviewUpdated = reviewRepositoryGRPC.update(id, review);
        assertFalse(isReviewEmpty(reviewUpdated));
        assertEquals(reviewResponse, reviewsUtil.toReviewResponse(reviewUpdated));

        verify(blockingStub, times(1)).update(any(UpdateReviewRequest.class));
    }

    @Test
    void shouldReturnEmptyResponseWhenUpdate() {

        var id = "1";
        var review = easyRandom.nextObject(Review.class);

        var reviewResponse = ReviewResponse.newBuilder().build();

        when(blockingStub.update(any(UpdateReviewRequest.class))).thenReturn(reviewResponse);

        var reviewUpdated = reviewRepositoryGRPC.update(id, review);
        assertTrue(isReviewEmpty(reviewUpdated));

        verify(blockingStub, times(1)).update(any(UpdateReviewRequest.class));
    }

    @Test
    void shouldReturnReviewWhenDeleteById() {

        var id = "987";
        var reviewResponse = Empty.newBuilder().build();

        when(blockingStub.deleteById(any(IdRequest.class))).thenReturn(reviewResponse);

        reviewRepositoryGRPC.deleteById(id);

        verify(blockingStub, times(1)).deleteById(any(IdRequest.class));
    }

    private boolean isReviewEmpty(Review review) {
        if (review == null) {
            return true;
        }
        if (!Objects.requireNonNullElse(review.getId(), "").isEmpty()) {
            return false;
        }
        if (Objects.requireNonNullElse(review.getProduct(), new Product()).getId() == 0
                && Objects.requireNonNullElse(review.getUser(), new User()).getId() == 0
                && Objects.requireNonNullElse(review.getRating(), 0) == 0) {
            return true;
        }
        return false;
    }
}