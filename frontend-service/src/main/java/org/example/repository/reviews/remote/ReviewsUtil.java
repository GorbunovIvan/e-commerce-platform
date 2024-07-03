package org.example.repository.reviews.remote;

import com.google.protobuf.Timestamp;
import jakarta.validation.constraints.NotNull;
import org.example.grpc.GrpcReviewServiceOuterClass.*;
import org.example.model.products.Product;
import org.example.model.reviews.ProductAndRatingInfo;
import org.example.model.reviews.Review;
import org.example.model.users.User;
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
        var review = new Review();
        review.setId(reviewResponse.getId());
        review.setUser(toUserFromUserId(reviewResponse.getUserId()));
        review.setProduct(toProductFromProductId(reviewResponse.getProductId()));
        review.setRating(reviewResponse.getRating());
        review.setCreatedAt(toLocalDateTime(reviewResponse.getCreatedAt()));
        return review;
    }

    public List<Review> toReviews(@NotNull ReviewsResponse reviewResponses) {
        return toReviews(reviewResponses.getReviewsList());
    }

    public List<Review> toReviews(@NotNull List<ReviewResponse> reviewResponses) {
        return reviewResponses.stream()
                .map(this::toReview)
                .collect(Collectors.toList());
    }

    public Review toReview(@NotNull ReviewRequest reviewRequest) {
        var review = new Review();
        review.setUser(toUserFromUserId(reviewRequest.getUserId()));
        review.setProduct(toProductFromProductId(reviewRequest.getProductId()));
        review.setRating(reviewRequest.getRating());
        review.setCreatedAt(toLocalDateTime(reviewRequest.getCreatedAt()));
        return review;
    }

    public ReviewRequest toReviewRequest(@NotNull Review review) {
        return ReviewRequest.newBuilder()
                .setProductId(review.getProductId())
                .setUserId(review.getUserId())
                .setRating(review.getRating())
                .setCreatedAt(toTimestamp(review.getCreatedAt()))
                .build();
    }

    public ReviewResponse toReviewResponse(@NotNull Review review) {
        return ReviewResponse.newBuilder()
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
        var product = toProductFromProductId(productAndRatingInfoResponse.getProductId());
        var reviews = toReviews(productAndRatingInfoResponse.getReviewsList());
        return new ProductAndRatingInfo(product, reviews);
    }

    public List<ProductAndRatingInfo> toProductAndRatingInfoList(@NotNull List<ProductAndRatingInfoResponse> productAndRatingInfoResponses) {
        return productAndRatingInfoResponses.stream()
                .map(this::toProductAndRatingInfo)
                .collect(Collectors.toList());
    }

    public List<ProductAndRatingInfo> toProductAndRatingInfoList(@NotNull ProductAndRatingInfoListResponse productAndRatingInfoListResponse) {
        return toProductAndRatingInfoList(productAndRatingInfoListResponse.getProductsInfoList());
    }

    private Product toProductFromProductId(Long productId) {
        var product = new Product();
        product.setId(productId);
        return product;
    }

    private User toUserFromUserId(Long userId) {
        var user = new User();
        user.setId(userId);
        return user;
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return Timestamp.newBuilder().build();
        }
        dateTime = dateTime.truncatedTo(ChronoUnit.SECONDS);
        var zonedDateTime = dateTime.atZone(zoneId);
        var instant = zonedDateTime.toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .build();
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        var instant = Instant.ofEpochSecond(timestamp.getSeconds());
        var zonedDateTime = instant.atZone(zoneId);
        return zonedDateTime.toLocalDateTime();
    }
}
