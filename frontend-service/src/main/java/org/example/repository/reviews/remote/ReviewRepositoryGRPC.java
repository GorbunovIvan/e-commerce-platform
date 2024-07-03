package org.example.repository.reviews.remote;

import com.google.protobuf.Empty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.GrpcReviewServiceGrpc;
import org.example.grpc.GrpcReviewServiceOuterClass;
import org.example.grpc.GrpcReviewServiceOuterClass.IdRequest;
import org.example.model.reviews.ProductAndRatingInfo;
import org.example.model.reviews.Review;
import org.example.repository.reviews.ReviewRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Primary
@ConditionalOnBean(name = "blockingStub")
@ConditionalOnProperty(name = "review-service.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class ReviewRepositoryGRPC implements ReviewRepository {

    private final GrpcReviewServiceGrpc.GrpcReviewServiceBlockingStub blockingStub;
    private final ReviewsUtil reviewsUtil;

    @Override
    public Review getById(String id) {

        log.info("Searching for review with id={}", id);

        var request = IdRequest.newBuilder()
                .setId(id)
                .build();

        var response = blockingStub.getById(request);
        return reviewsUtil.toReview(response);
    }

    @Override
    public List<Review> getByIds(Set<String> ids) {

        log.info("Searching for reviews with ids={}", ids);

        var request = GrpcReviewServiceOuterClass.IdsRequest.newBuilder()
                .addAllIds(collectionToIdsRequest(ids))
                .build();

        var response = blockingStub.getByIds(request);
        return reviewsUtil.toReviews(response);
    }

    @Override
    public List<Review> getAll() {
        log.info("Searching for all reviews");
        var request = Empty.newBuilder().build();
        var response = blockingStub.getAll(request);
        return reviewsUtil.toReviews(response);
    }

    @Override
    public ProductAndRatingInfo getRatingInfoOfProduct(Long productId) {

        log.info("Searching for average rating for productId={}", productId);

        var request = GrpcReviewServiceOuterClass.IdNumberRequest.newBuilder()
                .setId(productId)
                .build();

        var response = blockingStub.getRatingInfoOfProduct(request);
        return reviewsUtil.toProductAndRatingInfo(response);
    }

    @Override
    public List<ProductAndRatingInfo> getRatingInfoOfProducts(List<Long> productIds) {

        log.info("Searching for average rating for productIds in {}", productIds);

        var request = GrpcReviewServiceOuterClass.IdNumbersRequest.newBuilder()
                .addAllIds(collectionToIdNumbersRequest(productIds))
                .build();

        var response = blockingStub.getRatingInfoOfProducts(request);
        return reviewsUtil.toProductAndRatingInfoList(response);
    }

    @Override
    public List<Review> getAllByUser(Long userId) {

        log.info("Searching for reviews with userId={}", userId);

        var request = GrpcReviewServiceOuterClass.IdNumberRequest.newBuilder()
                .setId(userId)
                .build();

        var response = blockingStub.getAllByUser(request);
        return reviewsUtil.toReviews(response);
    }

    @Override
    public List<Review> getAllByUsers(List<Long> userIds) {

        log.info("Searching for reviews with userId in {}", userIds);

        var request = GrpcReviewServiceOuterClass.IdNumbersRequest.newBuilder()
                .addAllIds(collectionToIdNumbersRequest(userIds))
                .build();

        var response = blockingStub.getAllByUsers(request);
        return reviewsUtil.toReviews(response);
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

        var request = GrpcReviewServiceOuterClass.RatingRangeRequest.newBuilder()
                .setRatingMin(ratingMinFeatured)
                .setRatingMax(ratingMaxFeatured)
                .build();

        var response = blockingStub.getAllByRatingBetween(request);
        return reviewsUtil.toReviews(response);
    }

    @Override
    public Review create(Review review) {
        log.info("Creating review '{}'", review);
        var request = reviewsUtil.toReviewRequest(review);
        var response = blockingStub.create(request);
        return reviewsUtil.toReview(response);
    }

    @Override
    public Review update(String id, Review review) {

        log.info("Updating order with id={}, {}", id, review);

        var request = GrpcReviewServiceOuterClass.UpdateReviewRequest.newBuilder()
                .setId(id)
                .setReview(reviewsUtil.toReviewRequest(review))
                .build();

        var response = blockingStub.update(request);
        return reviewsUtil.toReview(response);
    }

    @Override
    public void deleteById(String id) {

        log.warn("Deleting review by id={}", id);

        var request = IdRequest.newBuilder()
                .setId(id)
                .build();

        //noinspection ResultOfMethodCallIgnored
        blockingStub.deleteById(request);
    }

    private List<GrpcReviewServiceOuterClass.IdNumberRequest> collectionToIdNumbersRequest(Collection<Long> ids) {
        var idNumbersRequest = new ArrayList<GrpcReviewServiceOuterClass.IdNumberRequest>();
        for (var id : ids) {
            idNumbersRequest.add(
                    GrpcReviewServiceOuterClass.IdNumberRequest.newBuilder()
                            .setId(id)
                            .build());
        }
        return idNumbersRequest;
    }

    private List<GrpcReviewServiceOuterClass.IdRequest> collectionToIdsRequest(Collection<String> ids) {
        var idsRequest = new ArrayList<GrpcReviewServiceOuterClass.IdRequest>();
        for (var id : ids) {
            idsRequest.add(
                    GrpcReviewServiceOuterClass.IdRequest.newBuilder()
                            .setId(id)
                            .build());
        }
        return idsRequest;
    }
}
