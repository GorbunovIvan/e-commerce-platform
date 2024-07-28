package org.example.repository.reviews;

import org.example.model.products.Product;
import org.example.model.reviews.ProductAndRatingInfo;
import org.example.model.reviews.Review;
import org.example.model.users.User;
import org.example.repository.ReflectUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReviewRepositoryDummyTest {

    @Autowired
    private ReviewRepositoryDummy reviewRepositoryDummy;

    @Autowired
    private ReflectUtil reflectUtil;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        setReviewsToReviewRepositoryDummy(new ArrayList<>());
    }

    @Test
    void shouldReturnReviewWhenGetById() {

        var reviews = easyRandom.objects(Review.class, 7).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var review = reviews.getFirst();
        var id = review.getId();

        var reviewReceived = reviewRepositoryDummy.getById(id);
        assertNotNull(reviewReceived);
        assertEquals(review, reviewReceived);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var reviews = easyRandom.objects(Review.class, 7).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var id = "99";

        var reviewReceived = reviewRepositoryDummy.getById(id);
        assertNull(reviewReceived);
    }

    @Test
    void shouldReturnReviewsWhenGetByIds() {

        var reviews = easyRandom.objects(Review.class, 7).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var ids = reviews.stream().map(Review::getId).collect(Collectors.toSet());

        var reviewReceived = reviewRepositoryDummy.getByIds(ids);
        assertNotNull(reviewReceived);
        assertFalse(reviewReceived.isEmpty());
        assertEquals(reviews, reviewReceived);
    }

    @Test
    void shouldReturnEmptyListWhenGetByIds() {

        var reviews = easyRandom.objects(Review.class, 7).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var ids = easyRandom.objects(String.class, 7).collect(Collectors.toSet());

        var reviewReceived = reviewRepositoryDummy.getByIds(ids);
        assertNotNull(reviewReceived);
        assertTrue(reviewReceived.isEmpty());
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAll() {

        var reviews = easyRandom.objects(Review.class, 7).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var reviewsReceived = reviewRepositoryDummy.getAll();
        assertNotNull(reviewsReceived);
        assertEquals(new HashSet<>(reviews), new HashSet<>(reviewsReceived));
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {
        var reviewsReceived = reviewRepositoryDummy.getAll();
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());
    }

    @Test
    void shouldReturnProductAndRatingInfoWhenGetRatingInfoOfProduct() {

        var product = easyRandom.nextObject(Product.class);

        var reviews = easyRandom.objects(Review.class, 7).toList();
        reviews.forEach(review -> review.setProduct(product));
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        productAndRatingInfo.setProduct(product);
        productAndRatingInfo.setReviews(reviews);

        var productAndRatingInfoReceived = reviewRepositoryDummy.getRatingInfoOfProduct(product.getId());
        assertNotNull(productAndRatingInfoReceived);
        assertEquals(productAndRatingInfo, productAndRatingInfoReceived);
        assertEquals(reviews, productAndRatingInfoReceived.getReviews());
    }

    @Test
    void shouldReturnProductAndRatingInfoWithEmptyReviewsWhenGetRatingInfoOfProduct() {

        var product = easyRandom.nextObject(Product.class);

        var reviews = easyRandom.objects(Review.class, 5).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        productAndRatingInfo.setProduct(product);
        productAndRatingInfo.setReviews(Collections.emptyList());

        var productAndRatingInfoReceived = reviewRepositoryDummy.getRatingInfoOfProduct(product.getId());
        assertNotNull(productAndRatingInfoReceived);
        assertEquals(productAndRatingInfo.getProductId(), productAndRatingInfoReceived.getProductId());
        assertTrue(productAndRatingInfoReceived.getReviews().isEmpty());
    }

    @Test
    void shouldReturnListOfProductAndRatingInfoWhenGetRatingInfoOfProducts() {

        var productAndRatingInfoList = new ArrayList<ProductAndRatingInfo>();
        var reviewsAll = new HashSet<Review>();

        var products = easyRandom.objects(Product.class, 3).toList();
        var productsIds = products.stream().map(Product::getId).toList();

        for (var product : products) {

            var reviews = easyRandom.objects(Review.class, 2).toList();
            reviews.forEach(review -> review.setProduct(product));

            reviewsAll.addAll(reviews);

            var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
            productAndRatingInfo.setProduct(product);
            productAndRatingInfo.setReviews(reviews);

            productAndRatingInfoList.add(productAndRatingInfo);
        }

        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviewsAll));

        var productAndRatingInfoReceived = reviewRepositoryDummy.getRatingInfoOfProducts(productsIds);
        assertNotNull(productAndRatingInfoReceived);
        assertEquals(new HashSet<>(productAndRatingInfoList), new HashSet<>(productAndRatingInfoReceived));

        var reviewsAllReceived = productAndRatingInfoReceived.stream()
                .map(ProductAndRatingInfo::getReviews)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        assertEquals(reviewsAll, reviewsAllReceived);
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByUser() {

        var user = easyRandom.nextObject(User.class);

        var reviews = easyRandom.objects(Review.class, 3).toList();
        reviews.forEach(review -> review.setUser(user));
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var reviewsReceived = reviewRepositoryDummy.getAllByUser(user.getId());
        assertNotNull(reviewsReceived);
        assertEquals(reviews, reviewsReceived);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUser() {

        var userId = 99L;

        var reviews = easyRandom.objects(Review.class, 3).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var reviewsReceived = reviewRepositoryDummy.getAllByUser(userId);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByUsers() {

        var reviewsAll = new ArrayList<Review>();

        var users = easyRandom.objects(User.class, 3).toList();
        var usersIds = users.stream().map(User::getId).toList();

        for (var user : users) {
            var reviews = easyRandom.objects(Review.class, 2).toList();
            reviews.forEach(review -> review.setUser(user));
            reviewsAll.addAll(reviews);
        }

        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviewsAll));

        var reviewsReceived = reviewRepositoryDummy.getAllByUsers(usersIds);
        assertNotNull(reviewsReceived);
        assertEquals(reviewsAll, reviewsReceived);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUsers() {

        var reviews = easyRandom.objects(Review.class, 7).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var userIdsAll = easyRandom.objects(Long.class, 5).toList();

        var reviewsReceived = reviewRepositoryDummy.getAllByUsers(userIdsAll);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByRatingBetween() {

        var reviews = easyRandom.objects(Review.class, 4).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var ratings = reviews.stream().map(Review::getRating).sorted().toList();

        var ratingMin = ratings.getFirst();
        var ratingMax = ratings.getLast();

        var reviewsReceived = reviewRepositoryDummy.getAllByRatingBetween(ratingMin, ratingMax);
        assertNotNull(reviewsReceived);
        assertEquals(reviews, reviewsReceived);
    }

    @Test
    void shouldReturnEmptyListIfRatingMinBiggerThanRatingMaxWhenGetAllByRatingBetween() {

        var reviews = easyRandom.objects(Review.class, 4).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var ratingMin = 2;
        var ratingMax = 1;

        var reviewsReceived = reviewRepositoryDummy.getAllByRatingBetween(ratingMin, ratingMax);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());
    }

    @Test
    void shouldReturnReviewWhenCreate() {

        var reviews = easyRandom.objects(Review.class, 4).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var review = easyRandom.nextObject(Review.class);

        var reviewCreated = reviewRepositoryDummy.create(review);
        assertNotNull(reviewCreated);
        assertEquals(review, reviewCreated);

        var reviewsAfterOperation = getReviewsFromReviewRepositoryDummy();
        assertTrue(reviewsAfterOperation.contains(review));
        assertEquals(reviews.size() + 1, reviewsAfterOperation.size());
    }

    @Test
    void shouldReturnReviewWhenUpdate() {

        var reviews = easyRandom.objects(Review.class, 4).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var review = reviews.getFirst();
        var id = review.getId();

        var reviewUpdated = reviewRepositoryDummy.update(id, review);
        assertNotNull(reviewUpdated);
        assertEquals(review, reviewUpdated);

        var reviewsAfterOperation = getReviewsFromReviewRepositoryDummy();
        assertTrue(reviewsAfterOperation.contains(review));
        assertEquals(reviews.size(), reviewsAfterOperation.size());
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var reviews = easyRandom.objects(Review.class, 4).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var id = "1";

        var reviewUpdated = reviewRepositoryDummy.update(id, new Review());
        assertNull(reviewUpdated);
    }

    @Test
    void shouldDeleteReviewWhenDeleteById() {

        var reviews = easyRandom.objects(Review.class, 4).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var reviewToDelete = reviews.getFirst();
        var id = reviewToDelete.getId();

        reviewRepositoryDummy.deleteById(id);

        var reviewsAfterOperation = getReviewsFromReviewRepositoryDummy();
        assertFalse(reviewsAfterOperation.contains(reviewToDelete));
        assertEquals(reviews.size() - 1, reviewsAfterOperation.size());
    }

    @Test
    void shouldNotDeleteReviewWhenDeleteById() {

        var reviews = easyRandom.objects(Review.class, 4).toList();
        setReviewsToReviewRepositoryDummy(new ArrayList<>(reviews));

        var id = "222";

        reviewRepositoryDummy.deleteById(id);

        var reviewsAfterOperation = getReviewsFromReviewRepositoryDummy();
        assertEquals(reviews.size(), reviewsAfterOperation.size());
    }

    private List<Review> getReviewsFromReviewRepositoryDummy() {
        return reflectUtil.getValueOfObjectField(reviewRepositoryDummy, "reviews", Collections::emptyList);
    }

    private void setReviewsToReviewRepositoryDummy(List<Review> reviews) {
        reflectUtil.setValueToObjectField(reviewRepositoryDummy, "reviews", reviews);
    }
}