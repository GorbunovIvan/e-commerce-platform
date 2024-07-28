package org.example.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.GrpcReviewServiceGrpc;
import org.example.grpc.GrpcReviewServiceOuterClass.*;
import org.example.model.ReviewDTO;

import java.util.ArrayList;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class GrpcReviewService extends GrpcReviewServiceGrpc.GrpcReviewServiceImplBase {

    private final ReviewService reviewService;
    private final ReviewsUtil reviewsUtil;

    @Override
    public void getById(IdRequest request, StreamObserver<ReviewResponse> responseObserver) {

        log.info("GRPC - GrpcReviewService.getById({}):", request);

        String id = null;

        try {
            id = request.getId();
            var review = reviewService.getById(id);

            ReviewResponse response;

            if (review == null) {
                response = ReviewResponse.newBuilder().build();
            } else {
                response = reviewsUtil.toReviewResponse(review);
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("GRPC - GrpcReviewService.getById({}) - attempt failed! {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void getByIds(IdsRequest request, StreamObserver<ReviewsResponse> responseObserver) {

        log.info("GRPC - GrpcReviewService.getByIds({}):", request);

        List<IdRequest> reviewIds = new ArrayList<>();

        try {
            reviewIds = request.getIdsList();
            var ids = reviewIds.stream().map(IdRequest::getId).toList();

            var reviews = reviewService.getByIds(ids);
            var reviewsResponses = reviewsUtil.toReviewsResponses(reviews);

            var response = ReviewsResponse.newBuilder()
                    .addAllReviews(reviewsResponses)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("GRPC - GrpcReviewService.getByIds({}) - attempt failed! {}", reviewIds, e.getMessage());
            throw e;
        }
    }

    @Override
    public void getAll(Empty request, StreamObserver<ReviewsResponse> responseObserver) {

        log.info("GRPC - GrpcReviewService.getAll():");

        try {
            var reviews = reviewService.getAll();
            var reviewsResponses = reviewsUtil.toReviewsResponses(reviews);

            var response = ReviewsResponse.newBuilder()
                    .addAllReviews(reviewsResponses)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("GRPC - GrpcReviewService.getAll() - attempt failed! {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public void getRatingInfoOfProduct(IdNumberRequest request, StreamObserver<ProductAndRatingInfoResponse> responseObserver) {

        log.info("GRPC - GrpcReviewService.getRatingInfoOfProduct({}):", request);

        Long id = null;

        try {
            id = request.getId();
            var productAndRatingInfo = reviewService.getRatingInfoOfProduct(id);
            var response = reviewsUtil.toProductAndRatingInfoResponse(productAndRatingInfo);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("GRPC - GrpcReviewService.getRatingInfoOfProduct({}) - attempt failed! {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void getRatingInfoOfProducts(IdNumbersRequest request, StreamObserver<ProductAndRatingInfoListResponse> responseObserver) {

        log.info("GRPC - GrpcReviewService.getRatingInfoOfProducts({}):", request);

        List<IdNumberRequest> productIds = new ArrayList<>();

        try {
            productIds = request.getIdsList();

            var ids = productIds.stream().map(IdNumberRequest::getId).toList();
            var productAndRatingInfoList = reviewService.getRatingInfoOfProducts(ids);

            var productAndRatingInfoResponses = reviewsUtil.toProductAndRatingInfoResponses(productAndRatingInfoList);

            var response = ProductAndRatingInfoListResponse.newBuilder()
                    .addAllProductsInfo(productAndRatingInfoResponses)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("GRPC - GrpcReviewService.getRatingInfoOfProducts({}) - attempt failed! {}", productIds, e.getMessage());
            throw e;
        }
    }

    @Override
    public void getAllByUser(IdNumberRequest request, StreamObserver<ReviewsResponse> responseObserver) {

        log.info("GRPC - GrpcReviewService.getAllByUser({}):", request);

        Long id = null;

        try {
            id = request.getId();
            var reviews = reviewService.getAllByUser(id);
            var reviewsResponses = reviewsUtil.toReviewsResponses(reviews);

            var response = ReviewsResponse.newBuilder()
                    .addAllReviews(reviewsResponses)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("GRPC - GrpcReviewService.getAllByUser({}) - attempt failed! {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void getAllByUsers(IdNumbersRequest request, StreamObserver<ReviewsResponse> responseObserver) {

        log.info("GRPC - GrpcReviewService.getAllByUsers({}):", request);

        List<IdNumberRequest> userIds = new ArrayList<>();

        try {
            userIds = request.getIdsList();
            var ids = userIds.stream().map(IdNumberRequest::getId).toList();

            var reviews = reviewService.getAllByUsers(ids);
            var reviewsResponses = reviewsUtil.toReviewsResponses(reviews);

            var response = ReviewsResponse.newBuilder()
                    .addAllReviews(reviewsResponses)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("GRPC - GrpcReviewService.getAllByUsers({}) - attempt failed! {}", userIds, e.getMessage());
            throw e;
        }
    }

    @Override
    public void getAllByRatingBetween(RatingRangeRequest request, StreamObserver<ReviewsResponse> responseObserver) {

        log.info("GRPC - GrpcReviewService.getAllByRatingBetween({}):", request);

        Integer ratingMin = null;
        Integer ratingMax = null;

        try {
            ratingMin = request.getRatingMin();
            ratingMax = request.getRatingMax();
            var reviews = reviewService.getAllByRatingBetween(ratingMin, ratingMax);
            var reviewsResponses = reviewsUtil.toReviewsResponses(reviews);

            var response = ReviewsResponse.newBuilder()
                    .addAllReviews(reviewsResponses)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("GRPC - GrpcReviewService.getAllByRatingBetween({}, {}) - attempt failed! {}", ratingMin, ratingMax, e.getMessage());
            throw e;
        }
    }

    @Override
    public void create(ReviewRequest request, StreamObserver<ReviewResponse> responseObserver) {

        log.info("GRPC - GrpcReviewService.create({}):", request);

        ReviewDTO reviewDTO = null;

        try {
            reviewDTO = reviewsUtil.toReviewDTO(request);
            var reviewCreated = reviewService.create(reviewDTO);

            var response = reviewsUtil.toReviewResponse(reviewCreated);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("GRPC - GrpcReviewService.create({}) - attempt failed! {}", reviewDTO, e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(UpdateReviewRequest request, StreamObserver<ReviewResponse> responseObserver) {

        log.info("GRPC - GrpcReviewService.update({}):", request);

        String id = null;
        ReviewDTO reviewDTO = null;

        try {
            id = request.getId();
            reviewDTO = reviewsUtil.toReviewDTO(request.getReview());
            var reviewUpdated = reviewService.update(id, reviewDTO);

            var response = reviewsUtil.toReviewResponse(reviewUpdated);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("GRPC - GrpcReviewService.update({}, {}) - attempt failed! {}", id, reviewDTO, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteById(IdRequest request, StreamObserver<Empty> responseObserver) {

        log.info("GRPC - GrpcReviewService.deleteById({}):", request);

        String id = null;

        try {
            id = request.getId();
            reviewService.deleteById(id);

            var response = Empty.newBuilder().build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("GRPC - GrpcReviewService.deleteById({}) - attempt failed! {}", id, e.getMessage());
            throw e;
        }
    }
}
