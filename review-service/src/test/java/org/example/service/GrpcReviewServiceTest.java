package org.example.service;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.grpc.GrpcReviewServiceGrpc;
import org.example.grpc.GrpcReviewServiceOuterClass.*;
import org.example.model.ProductAndRatingInfo;
import org.example.model.Review;
import org.example.model.ReviewDTO;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class GrpcReviewServiceTest {

    private ManagedChannel channel;

    private GrpcReviewServiceGrpc.GrpcReviewServiceBlockingStub blockingStub;

    @Value("${grpc.server.port}")
    private int grpcPort;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ReviewsUtil reviewsUtil;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {

        this.channel = ManagedChannelBuilder.forAddress("localhost", grpcPort)
                .usePlaintext()
                .build();

        this.blockingStub = GrpcReviewServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void tearDown() {
        channel.shutdown();
    }

    @Test
    void shouldReturnReviewWhenGetById() {

        var review = easyRandom.nextObject(Review.class);
        var id = review.getId();

        when(reviewService.getById(id)).thenReturn(review);

        var request = IdRequest.newBuilder().setId(id).build();
        var response = blockingStub.getById(request);

        var reviewReceived = reviewsUtil.toReview(response);
        assertNotNull(reviewReceived);

        assertEquals(review, reviewReceived);

        verify(reviewService, times(1)).getById(id);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = "99";

        var request = IdRequest.newBuilder().setId(id).build();
        var response = blockingStub.getById(request);

        assertTrue(response.getId().isEmpty());

        verify(reviewService, times(1)).getById(id);
    }

    @Test
    void shouldReturnReviewsWhenGetByIds() {

        var reviews = easyRandom.objects(Review.class, 7).toList();
        var ids = reviews.stream().map(Review::getId).toList();

        when(reviewService.getByIds(ids)).thenReturn(reviews);

        var request = IdsRequest.newBuilder()
                .addAllIds(listToIdsRequest(ids))
                .build();

        var response = blockingStub.getByIds(request);

        var reviewsReceived = reviewsUtil.toReviews(response);
        assertNotNull(reviewsReceived);
        assertFalse(reviewsReceived.isEmpty());
        assertEquals(new HashSet<>(reviews), new HashSet<>(reviewsReceived));

        verify(reviewService, times(1)).getByIds(ids);
    }

    @Test
    void shouldReturnEmptyListWhenGetByIds() {

        var ids = easyRandom.objects(String.class, 7).toList();

        when(reviewService.getByIds(ids)).thenReturn(Collections.emptyList());

        var request = IdsRequest.newBuilder()
                .addAllIds(listToIdsRequest(ids))
                .build();

        var response = blockingStub.getByIds(request);

        var reviewsReceived = reviewsUtil.toReviews(response);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(reviewService, times(1)).getByIds(ids);
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAll() {

        var reviews = easyRandom.objects(Review.class, 7).toList();

        when(reviewService.getAll()).thenReturn(reviews);

        var request = Empty.newBuilder().build();
        var response = blockingStub.getAll(request);

        var reviewsReceived = reviewsUtil.toReviews(response);
        assertNotNull(reviewsReceived);
        assertEquals(new HashSet<>(reviews), new HashSet<>(reviewsReceived));

        verify(reviewService, times(1)).getAll();
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {

        when(reviewService.getAll()).thenReturn(Collections.emptyList());

        var request = Empty.newBuilder().build();
        var response = blockingStub.getAll(request);

        var reviewsReceived = reviewsUtil.toReviews(response);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(reviewService, times(1)).getAll();
    }

    @Test
    void shouldReturnProductAndRatingInfoWhenGetRatingInfoOfProduct() {

        var productId = 98L;

        var reviews = easyRandom.objects(Review.class, 7).toList();
        reviews.forEach(review -> review.setProductId(productId));

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        productAndRatingInfo.setProductId(productId);
        productAndRatingInfo.setReviews(reviews);

        when(reviewService.getRatingInfoOfProduct(productId)).thenReturn(productAndRatingInfo);

        var request = IdNumberRequest.newBuilder()
                .setId(productId)
                .build();

        var response = blockingStub.getRatingInfoOfProduct(request);

        var productAndRatingInfoReceived = reviewsUtil.toProductAndRatingInfo(response);
        assertNotNull(productAndRatingInfoReceived);
        assertEquals(productAndRatingInfo, productAndRatingInfoReceived);
        assertEquals(productId, productAndRatingInfoReceived.getProductId());
        assertEquals(new HashSet<>(reviews), new HashSet<>(productAndRatingInfoReceived.getReviews()));

        verify(reviewService, times(1)).getRatingInfoOfProduct(productId);
    }

    @Test
    void shouldReturnProductAndRatingInfoWithEmptyReviewsWhenGetRatingInfoOfProduct() {

        var productId = 99L;

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        productAndRatingInfo.setProductId(productId);
        productAndRatingInfo.setReviews(Collections.emptyList());

        when(reviewService.getRatingInfoOfProduct(productId)).thenReturn(productAndRatingInfo);

        var request = IdNumberRequest.newBuilder()
                .setId(productId)
                .build();

        var response = blockingStub.getRatingInfoOfProduct(request);

        var productAndRatingInfoReceived = reviewsUtil.toProductAndRatingInfo(response);
        assertNotNull(productAndRatingInfoReceived);
        assertEquals(productAndRatingInfo, productAndRatingInfoReceived);
        assertEquals(productId, productAndRatingInfoReceived.getProductId());
        assertTrue(productAndRatingInfoReceived.getReviews().isEmpty());

        verify(reviewService, times(1)).getRatingInfoOfProduct(productId);
    }

    @Test
    void shouldReturnListOfProductAndRatingInfoWhenGetRatingInfoOfProducts() {

        var productIds = easyRandom.objects(Long.class, 3).toList();

        var productAndRatingInfoList = new ArrayList<ProductAndRatingInfo>();

        for (var productId : productIds) {

            var reviews = easyRandom.objects(Review.class, 5).toList();
            reviews.forEach(review -> review.setProductId(productId));

            var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
            productAndRatingInfo.setProductId(productId);
            productAndRatingInfo.setReviews(reviews);

            productAndRatingInfoList.add(productAndRatingInfo);
        }

        when(reviewService.getRatingInfoOfProducts(productIds)).thenReturn(productAndRatingInfoList);

        var request = IdNumbersRequest.newBuilder()
                .addAllIds(listToIdNumbersRequest(productIds))
                .build();

        var response = blockingStub.getRatingInfoOfProducts(request);

        var productAndRatingInfoListReceived = reviewsUtil.toProductAndRatingInfoList(response);
        assertNotNull(productAndRatingInfoListReceived);
        assertEquals(new HashSet<>(productAndRatingInfoList), new HashSet<>(productAndRatingInfoListReceived));

        verify(reviewService, times(1)).getRatingInfoOfProducts(any());
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByUser() {

        var userId = 98L;

        var reviews = easyRandom.objects(Review.class, 7).toList();
        reviews.forEach(review -> review.setUserId(userId));

        when(reviewService.getAllByUser(userId)).thenReturn(reviews);

        var request = IdNumberRequest.newBuilder()
                .setId(userId)
                .build();

        var response = blockingStub.getAllByUser(request);

        var reviewsReceived = reviewsUtil.toReviews(response);
        assertNotNull(reviewsReceived);
        assertEquals(new HashSet<>(reviews), new HashSet<>(reviewsReceived));

        verify(reviewService, times(1)).getAllByUser(userId);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUser() {

        var userId = 99L;

        when(reviewService.getAllByUser(userId)).thenReturn(Collections.emptyList());

        var request = IdNumberRequest.newBuilder()
                .setId(userId)
                .build();

        var response = blockingStub.getAllByUser(request);

        var reviewsReceived = reviewsUtil.toReviews(response);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(reviewService, times(1)).getAllByUser(userId);
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByUsers() {

        var userIdsAll = easyRandom.objects(Long.class, 5).toList();

        var reviewsAll = new ArrayList<Review>();

        for (var userId : userIdsAll) {

            var reviews = easyRandom.objects(Review.class, 3).toList();
            reviews.forEach(review -> review.setUserId(userId));

            reviewsAll.addAll(reviews);
        }

        Collections.shuffle(reviewsAll);

        when(reviewService.getAllByUsers(userIdsAll)).thenReturn(reviewsAll);

        var request = IdNumbersRequest.newBuilder()
                .addAllIds(listToIdNumbersRequest(userIdsAll))
                .build();

        var response = blockingStub.getAllByUsers(request);

        var reviewsReceived = reviewsUtil.toReviews(response);
        assertNotNull(reviewsReceived);
        assertEquals(new HashSet<>(reviewsAll), new HashSet<>(reviewsReceived));

        verify(reviewService, times(1)).getAllByUsers(userIdsAll);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUsers() {

        var userIdsAll = easyRandom.objects(Long.class, 5).toList();

        when(reviewService.getAllByUsers(userIdsAll)).thenReturn(Collections.emptyList());

        var request = IdNumbersRequest.newBuilder()
                .addAllIds(listToIdNumbersRequest(userIdsAll))
                .build();

        var response = blockingStub.getAllByUsers(request);

        var reviewsReceived = reviewsUtil.toReviews(response);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(reviewService, times(1)).getAllByUsers(userIdsAll);
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByRatingBetween() {

        var reviews = easyRandom.objects(Review.class, 4).toList();

        var ratingMin = 3;
        var ratingMax = 7;

        when(reviewService.getAllByRatingBetween(ratingMin, ratingMax)).thenReturn(reviews);

        var request = RatingRangeRequest.newBuilder()
                .setRatingMin(ratingMin)
                .setRatingMax(ratingMax)
                .build();

        var response = blockingStub.getAllByRatingBetween(request);

        var reviewsReceived = reviewsUtil.toReviews(response);
        assertNotNull(reviewsReceived);
        assertEquals(new HashSet<>(reviews), new HashSet<>(reviewsReceived));

        verify(reviewService, times(1)).getAllByRatingBetween(ratingMin, ratingMax);
    }

    @Test
    void shouldReturnReviewWhenCreate() {

        var reviewDTO = easyRandom.nextObject(ReviewDTO.class);
        var newId = "123";

        when(reviewService.create(any(ReviewDTO.class))).thenAnswer(ans -> {
            var reviewDTOParam = ans.getArgument(0, ReviewDTO.class);
            var review = reviewDTOParam.toReview();
            review.setId(newId);
            return review;
        });

        var request = reviewsUtil.toReviewRequest(reviewDTO);

        var response = blockingStub.create(request);

        var reviewCreated = reviewsUtil.toReview(response);
        assertNotNull(reviewCreated);
        assertNotNull(reviewCreated.getId());
        assertEquals(newId, reviewCreated.getId());
        assertEquals(reviewDTO.toReview(), reviewCreated);

        verify(reviewService, times(1)).create(any(ReviewDTO.class));
    }

    @Test
    void shouldReturnReviewWhenUpdate() {

        var id = "987";

        var reviewDTO = easyRandom.nextObject(ReviewDTO.class);

        var reviewExisting = reviewDTO.toReview();
        reviewExisting.setId(id);

        when(reviewService.update(anyString(), any(ReviewDTO.class))).thenReturn(reviewExisting);

        var request = UpdateReviewRequest.newBuilder()
                .setId(id)
                .setReview(reviewsUtil.toReviewRequest(reviewDTO))
                .build();

        var response = blockingStub.update(request);

        var reviewUpdated = reviewsUtil.toReview(response);
        assertNotNull(reviewUpdated);
        assertEquals(id, reviewUpdated.getId());
        assertEquals(reviewExisting, reviewUpdated);

        verify(reviewService, times(1)).update(anyString(), any(ReviewDTO.class));
    }

    @Test
    void shouldReturnReviewWhenDeleteById() {

        var id = "971";

        var request = IdRequest.newBuilder()
                .setId(id)
                .build();

        var response = blockingStub.deleteById(request);
        assertEquals(Empty.newBuilder().build(), response);

        verify(reviewService, times(1)).deleteById(id);
    }

    private List<IdNumberRequest> listToIdNumbersRequest(List<Long> ids) {
        var idNumbersRequest = new ArrayList<IdNumberRequest>();
        for (var id : ids) {
            idNumbersRequest.add(
                    IdNumberRequest.newBuilder()
                            .setId(id)
                            .build());
        }
        return idNumbersRequest;
    }

    private List<IdRequest> listToIdsRequest(List<String> ids) {
        var idsRequest = new ArrayList<IdRequest>();
        for (var id : ids) {
            idsRequest.add(
                    IdRequest.newBuilder()
                            .setId(id)
                            .build());
        }
        return idsRequest;
    }
}