package org.example.service;

import com.google.protobuf.Timestamp;
import jakarta.validation.constraints.NotNull;
import net.devh.boot.grpc.server.serverfactory.GrpcServerLifecycle;
import org.example.grpc.GrpcReviewServiceOuterClass.ReviewRequest;
import org.example.model.ProductAndRatingInfo;
import org.example.model.Review;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ReviewsUtilTest {

    @Autowired
    private ReviewsUtil reviewsUtil;

    @MockBean
    private GrpcServerLifecycle grpcServerLifecycle; // To ignore bean creation

    private final ZoneId zoneId = ZoneId.systemDefault();

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnReviewDTOWhenToReviewDTO() {

        var reviewRequest = ReviewRequest.newBuilder()
                .setProductId(easyRandom.nextObject(Long.class))
                .setUserId(easyRandom.nextObject(Long.class))
                .setRating(easyRandom.nextObject(Integer.class))
                .setCreatedAt(com.google.protobuf.Timestamp.getDefaultInstance().toBuilder().setSeconds(easyRandom.nextInt(1718269320, Integer.MAX_VALUE)))
                .build();

        var reviewDTO = reviewsUtil.toReviewDTO(reviewRequest);

        assertNotNull(reviewDTO);
        assertEquals(reviewRequest.getProductId(), reviewDTO.getProductId());
        assertEquals(reviewRequest.getUserId(), reviewDTO.getUserId());
        assertEquals(reviewRequest.getRating(), reviewDTO.getRating());
        assertEquals(toLocalDateTime(reviewRequest.getCreatedAt()), reviewDTO.getCreatedAt());
    }

    @Test
    void shouldReturnReviewResponseWhenToReviewResponse() {

        var review = easyRandom.nextObject(Review.class);

        var reviewResponse = reviewsUtil.toReviewResponse(review);

        assertNotNull(reviewResponse);
        assertEquals(review.getId(), reviewResponse.getId());
        assertEquals(review.getProductId(), reviewResponse.getProductId());
        assertEquals(review.getUserId(), reviewResponse.getUserId());
        assertEquals(review.getRating(), reviewResponse.getRating());
        assertEquals(toTimestamp(review.getCreatedAt()), reviewResponse.getCreatedAt());
    }

    @Test
    void shouldReturnListOfReviewResponsesWhenToReviewsResponses() {

        var reviews = easyRandom.objects(Review.class, 7).toList();

        var reviewResponses = reviewsUtil.toReviewsResponses(reviews);

        assertNotNull(reviewResponses);
        assertEquals(reviews.size(), reviewResponses.size());

        for (int i = 0; i < reviews.size(); i++) {

            var review = reviews.get(i);
            var reviewResponse = reviewResponses.get(i);

            assertEquals(review.getId(), reviewResponse.getId());
            assertEquals(review.getProductId(), reviewResponse.getProductId());
            assertEquals(review.getUserId(), reviewResponse.getUserId());
            assertEquals(review.getRating(), reviewResponse.getRating());
            assertEquals(toTimestamp(review.getCreatedAt()), reviewResponse.getCreatedAt());
        }
    }

    @Test
    void shouldReturnProductAndRatingInfoResponseWhenToProductAndRatingInfoResponse() {

        var productId = 99L;

        var reviews = easyRandom.objects(Review.class, 7).toList();
        reviews.forEach(review -> review.setProductId(productId));

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        productAndRatingInfo.setProductId(productId);
        productAndRatingInfo.setReviews(reviews);

        var productAndRatingInfoResponse = reviewsUtil.toProductAndRatingInfoResponse(productAndRatingInfo);

        assertNotNull(productAndRatingInfoResponse);
        assertEquals(productAndRatingInfo.getProductId(), productAndRatingInfoResponse.getProductId());
        assertEquals(productAndRatingInfo.getRating(), productAndRatingInfoResponse.getRating());
        assertEquals(productAndRatingInfo.getNumberOfReviews(), productAndRatingInfoResponse.getNumberOfReviews());
        assertEquals(productAndRatingInfo.getReviews().size(), productAndRatingInfoResponse.getReviewsCount());
    }

    @Test
    void shouldReturnListOfProductAndRatingInfoResponsesWhenToProductAndRatingInfoResponses() {

        var productAndRatingInfoList = easyRandom.objects(ProductAndRatingInfo.class, 10).toList();

        for (var productAndRatingInfo : productAndRatingInfoList) {
            var reviews = easyRandom.objects(Review.class, 7).toList();
            reviews.forEach(review -> review.setProductId(productAndRatingInfo.getProductId()));
            productAndRatingInfo.setReviews(reviews);
        }

        var productAndRatingInfoResponses = reviewsUtil.toProductAndRatingInfoResponses(productAndRatingInfoList);

        assertNotNull(productAndRatingInfoResponses);
        assertEquals(productAndRatingInfoList.size(), productAndRatingInfoResponses.size());

        for (int i = 0; i < productAndRatingInfoList.size(); i++) {

            var productAndRatingInfo = productAndRatingInfoList.get(i);
            var productAndRatingInfoResponse = productAndRatingInfoResponses.get(i);

            assertEquals(productAndRatingInfo.getProductId(), productAndRatingInfoResponse.getProductId());
            assertEquals(productAndRatingInfo.getRating(), productAndRatingInfoResponse.getRating());
            assertEquals(productAndRatingInfo.getNumberOfReviews(), productAndRatingInfoResponse.getNumberOfReviews());
            assertEquals(productAndRatingInfo.getReviews().size(), productAndRatingInfoResponse.getReviewsCount());
        }
    }

    private Timestamp toTimestamp(@NotNull LocalDateTime dateTime) {
        dateTime = dateTime.truncatedTo(ChronoUnit.SECONDS);
        var zonedDateTime = dateTime.atZone(zoneId);
        var instant = zonedDateTime.toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .build();
    }

    private LocalDateTime toLocalDateTime(@NotNull Timestamp timestamp) {
        var instant = Instant.ofEpochSecond(timestamp.getSeconds());
        var zonedDateTime = instant.atZone(zoneId);
        return zonedDateTime.toLocalDateTime();
    }
}