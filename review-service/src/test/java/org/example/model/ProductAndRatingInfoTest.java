package org.example.model;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ProductAndRatingInfoTest {

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnDoubleWhenGetRating() {

        var productId = 98L;

        var reviews = easyRandom.objects(Review.class, 7).toList();
        reviews.forEach(review -> review.setProductId(productId));

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        productAndRatingInfo.setProductId(productId);
        productAndRatingInfo.setReviews(reviews);

        var ratingSum = 0;

        for (var review : reviews) {
            ratingSum += review.getRating();
        }

        double ratingExpected = (double) ratingSum / reviews.size();
        double ratingReceived = productAndRatingInfo.getRating();

        assertEquals(ratingExpected, ratingReceived);
    }

    @Test
    void shouldReturnZeroWhenGetRating() {

        var productId = 98L;

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        productAndRatingInfo.setProductId(productId);
        productAndRatingInfo.setReviews(Collections.emptyList());

        var ratingReceived = productAndRatingInfo.getRating();

        assertEquals(-1D, ratingReceived);
    }

    @Test
    void shouldReturnListOfProductAndRatingInfoWhenReviewsToProductAndRatings() {

        var productIdsAll = easyRandom.objects(Long.class, 10).toList();

        var reviewsAll = new ArrayList<Review>();

        for (var productId : productIdsAll) {

            var reviews = easyRandom.objects(Review.class, 5).toList();
            reviews.forEach(review -> review.setProductId(productId));

            reviewsAll.addAll(reviews);
        }

        Collections.shuffle(reviewsAll);

        var productAndRatingInfoListReceived = ProductAndRatingInfo.reviewsToProductAndRatings(reviewsAll);

        assertNotNull(productAndRatingInfoListReceived);
        assertEquals(productIdsAll.size(), productAndRatingInfoListReceived.size());
        assertEquals(new HashSet<>(reviewsAll), productAndRatingInfoListReceived.stream().map(ProductAndRatingInfo::getReviews).flatMap(Collection::stream).collect(Collectors.toSet()));

        for (var productId : productIdsAll) {

            var productAndRatingInfoReceived = productAndRatingInfoListReceived.stream()
                    .filter(pInfo -> pInfo.getProductId().equals(productId))
                    .findAny()
                    .orElse(null);

            assertNotNull(productAndRatingInfoReceived);

            var reviewsExpected = reviewsAll.stream()
                    .filter(review -> review.getProductId().equals(productId))
                    .collect(Collectors.toSet());

            assertEquals(reviewsExpected, new HashSet<>(productAndRatingInfoReceived.getReviews()));
        }
    }

    @Test
    void shouldReturnEmptyListOfProductAndRatingInfoWhenReviewsToProductAndRatings() {

        var productAndRatingInfoListReceived = ProductAndRatingInfo.reviewsToProductAndRatings(Collections.emptyList());

        assertNotNull(productAndRatingInfoListReceived);
        assertTrue(productAndRatingInfoListReceived.isEmpty());
    }
}