package org.example.service;

import com.google.protobuf.Timestamp;
import jakarta.validation.constraints.NotNull;
import org.example.grpc.GrpcReviewServiceOuterClass;
import org.example.grpc.GrpcReviewServiceOuterClass.*;
import org.example.model.ProductAndRatingInfo;
import org.example.model.Review;
import org.example.model.ReviewDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewsUtil {

    private final ZoneId zoneId = ZoneId.systemDefault();

    public Review toReview(@NotNull ReviewResponse reviewResponse) {
        return new Review(
                reviewResponse.getId(),
                reviewResponse.getProductId(),
                reviewResponse.getUserId(),
                reviewResponse.getRating(),
                toLocalDateTime(reviewResponse.getCreatedAt()));
    }

    public List<Review> toReviews(@NotNull ReviewsResponse reviewResponses) {
        return toReviews(reviewResponses.getReviewsList());
    }

    public List<Review> toReviews(@NotNull List<ReviewResponse> reviewResponses) {
        return reviewResponses.stream()
                .map(this::toReview)
                .collect(Collectors.toList());
    }

    public ReviewDTO toReviewDTO(@NotNull ReviewRequest reviewRequest) {
        return new ReviewDTO(
                reviewRequest.getProductId(),
                reviewRequest.getUserId(),
                reviewRequest.getRating(),
                toLocalDateTime(reviewRequest.getCreatedAt()));
    }

    public ReviewRequest toReviewRequest(@NotNull ReviewDTO reviewDTO) {
        return ReviewRequest.newBuilder()
                .setProductId(reviewDTO.getProductId())
                .setUserId(reviewDTO.getUserId())
                .setRating(reviewDTO.getRating())
                .setCreatedAt(toTimestamp(reviewDTO.getCreatedAt()))
                .build();
    }

    public ReviewResponse toReviewResponse(@NotNull Review review) {
        return GrpcReviewServiceOuterClass.ReviewResponse.newBuilder()
                .setId(review.getId())
                .setProductId(review.getProductId())
                .setUserId(review.getUserId())
                .setRating(review.getRating())
                .setCreatedAt(toTimestamp(review.getCreatedAt()))
                .build();
    }

    public List<ReviewResponse> toReviewsResponses(@NotNull List<Review> reviews) {
        return reviews.stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }

    public ProductAndRatingInfoResponse toProductAndRatingInfoResponse(@NotNull ProductAndRatingInfo productAndRatingInfo) {
        return ProductAndRatingInfoResponse.newBuilder()
                .setProductId(productAndRatingInfo.getProductId())
                .setRating(productAndRatingInfo.getRating())
                .setNumberOfReviews(productAndRatingInfo.getNumberOfReviews())
                .addAllReviews(toReviewsResponses(productAndRatingInfo.getReviews()))
                .build();
    }

    public List<ProductAndRatingInfoResponse> toProductAndRatingInfoResponses(@NotNull List<ProductAndRatingInfo> productAndRatingInfoList) {
        return productAndRatingInfoList.stream()
                .map(this::toProductAndRatingInfoResponse)
                .collect(Collectors.toList());
    }

    public ProductAndRatingInfo toProductAndRatingInfo(@NotNull ProductAndRatingInfoResponse productAndRatingInfoResponse) {
        return new ProductAndRatingInfo(
                productAndRatingInfoResponse.getProductId(),
                this.toReviews(productAndRatingInfoResponse.getReviewsList()));
    }

    public List<ProductAndRatingInfo> toProductAndRatingInfoList(@NotNull List<ProductAndRatingInfoResponse> productAndRatingInfoResponses) {
        return productAndRatingInfoResponses.stream()
                .map(this::toProductAndRatingInfo)
                .collect(Collectors.toList());
    }

    public List<ProductAndRatingInfo> toProductAndRatingInfoList(@NotNull ProductAndRatingInfoListResponse productAndRatingInfoListResponse) {
        return this.toProductAndRatingInfoList(productAndRatingInfoListResponse.getProductsInfoList());
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
