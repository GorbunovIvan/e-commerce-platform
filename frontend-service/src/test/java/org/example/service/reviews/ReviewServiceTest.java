package org.example.service.reviews;

import org.example.model.products.Product;
import org.example.model.reviews.ProductAndRatingInfo;
import org.example.model.reviews.Review;
import org.example.model.users.User;
import org.example.repository.reviews.ReviewRepository;
import org.example.service.modelsBinding.ModelBinder;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @MockBean
    private ReviewRepository reviewRepository;
    @MockBean
    private ModelBinder modelBinder;
    
    private final EasyRandom easyRandom = new EasyRandom();
    
    @BeforeEach
    void setUp() {
        when(modelBinder.bindFields(any())).thenAnswer(ans -> ans.getArgument(0));
    }

    @Test
    void shouldReturnReviewWhenGetById() {

        var review = easyRandom.nextObject(Review.class);
        var id = review.getId();

        when(reviewRepository.getById(id)).thenReturn(review);

        var reviewReceived = reviewService.getById(id);
        assertNotNull(reviewReceived);
        assertEquals(review, reviewReceived);

        verify(reviewRepository, times(1)).getById(id);
        verify(modelBinder, times(1)).bindFields(review);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = "99";

        when(reviewRepository.getById(id)).thenReturn(null);

        var reviewReceived = reviewService.getById(id);
        assertNull(reviewReceived);

        verify(reviewRepository, times(1)).getById(id);
        verify(modelBinder, times(1)).bindFields(null);
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAll() {

        var reviews = easyRandom.objects(Review.class, 7).toList();

        when(reviewRepository.getAll()).thenReturn(reviews);

        var reviewsReceived = reviewService.getAll();
        assertNotNull(reviewsReceived);
        assertEquals(reviews, reviewsReceived);

        verify(reviewRepository, times(1)).getAll();
        verify(modelBinder, times(1)).bindFields(reviews);
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {

        when(reviewRepository.getAll()).thenReturn(Collections.emptyList());

        var reviewsReceived = reviewService.getAll();
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(reviewRepository, times(1)).getAll();
        verify(modelBinder, times(1)).bindFields(Collections.emptyList());
    }

    @Test
    void shouldReturnProductAndRatingInfoWhenGetRatingInfoOfProduct() {

        var product = easyRandom.nextObject(Product.class);

        var reviews = easyRandom.objects(Review.class, 7).toList();
        reviews.forEach(review -> review.setProduct(product));

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        productAndRatingInfo.setProduct(product);
        productAndRatingInfo.setReviews(reviews);

        when(reviewRepository.getRatingInfoOfProduct(product.getId())).thenReturn(productAndRatingInfo);

        var productAndRatingInfoReceived = reviewService.getRatingInfoOfProduct(product);
        assertNotNull(productAndRatingInfoReceived);
        assertEquals(productAndRatingInfo, productAndRatingInfoReceived);
        assertEquals(reviews, productAndRatingInfoReceived.getReviews());

        verify(reviewRepository, times(1)).getRatingInfoOfProduct(product.getId());
        verify(modelBinder, times(1)).bindFields(productAndRatingInfo);
    }

    @Test
    void shouldReturnProductAndRatingInfoWithEmptyReviewsWhenGetRatingInfoOfProduct() {

        var product = easyRandom.nextObject(Product.class);

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        productAndRatingInfo.setProduct(product);
        productAndRatingInfo.setReviews(Collections.emptyList());

        when(reviewRepository.getRatingInfoOfProduct(product.getId())).thenReturn(productAndRatingInfo);

        var productAndRatingInfoReceived = reviewService.getRatingInfoOfProduct(product.getId());
        assertNotNull(productAndRatingInfoReceived);
        assertEquals(productAndRatingInfo, productAndRatingInfoReceived);
        assertTrue(productAndRatingInfoReceived.getReviews().isEmpty());

        verify(reviewRepository, times(1)).getRatingInfoOfProduct(product.getId());
        verify(modelBinder, times(1)).bindFields(productAndRatingInfo);
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

        when(reviewRepository.getRatingInfoOfProducts(productsIds)).thenReturn(productAndRatingInfoList);

        var productAndRatingInfoReceived = reviewService.getRatingInfoOfProducts(productsIds);
        assertNotNull(productAndRatingInfoReceived);
        assertEquals(productAndRatingInfoList, productAndRatingInfoReceived);

        var reviewsAllReceived = productAndRatingInfoReceived.stream()
                .map(ProductAndRatingInfo::getReviews)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        assertEquals(reviewsAll, reviewsAllReceived);

        verify(reviewRepository, times(1)).getRatingInfoOfProducts(productsIds);
        verify(modelBinder, times(1)).bindFields(productAndRatingInfoList);
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByUser() {

        var user = easyRandom.nextObject(User.class);

        var reviews = easyRandom.objects(Review.class, 3).toList();
        reviews.forEach(review -> review.setUser(user));

        when(reviewRepository.getAllByUser(user.getId())).thenReturn(reviews);

        var reviewsReceived = reviewService.getAllByUser(user);
        assertNotNull(reviewsReceived);
        assertEquals(reviews, reviewsReceived);

        verify(reviewRepository, times(1)).getAllByUser(user.getId());
        verify(modelBinder, times(1)).bindFields(reviews);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUser() {

        var userId = 99L;

        when(reviewRepository.getAllByUser(userId)).thenReturn(Collections.emptyList());

        var reviewsReceived = reviewService.getAllByUser(userId);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(reviewRepository, times(1)).getAllByUser(userId);
        verify(modelBinder, times(1)).bindFields(Collections.emptyList());
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

        when(reviewRepository.getAllByUsers(usersIds)).thenReturn(reviewsAll);

        var reviewsReceived = reviewService.getAllByUsers(usersIds);
        assertNotNull(reviewsReceived);
        assertEquals(reviewsAll, reviewsReceived);

        verify(reviewRepository, times(1)).getAllByUsers(usersIds);
        verify(modelBinder, times(1)).bindFields(reviewsAll);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUsers() {

        var userIdsAll = easyRandom.objects(Long.class, 10).toList();

        when(reviewRepository.getAllByUsers(userIdsAll)).thenReturn(Collections.emptyList());

        var reviewsReceived = reviewService.getAllByUsers(userIdsAll);
        assertNotNull(reviewsReceived);
        assertTrue(reviewsReceived.isEmpty());

        verify(reviewRepository, times(1)).getAllByUsers(userIdsAll);
        verify(modelBinder, times(1)).bindFields(Collections.emptyList());
    }

    @Test
    void shouldReturnListOfReviewsWhenGetAllByRatingBetween() {

        var reviews = easyRandom.objects(Review.class, 10).toList();

        var ratingMin = 3;
        var ratingMax = 7;

        when(reviewRepository.getAllByRatingBetween(ratingMin, ratingMax)).thenReturn(reviews);

        var reviewsReceived = reviewService.getAllByRatingBetween(ratingMin, ratingMax);
        assertNotNull(reviewsReceived);
        assertEquals(reviews, reviewsReceived);

        verify(reviewRepository, times(1)).getAllByRatingBetween(ratingMin, ratingMax);
        verify(modelBinder, times(1)).bindFields(reviews);
    }

    @Test
    void shouldReturnReviewWhenCreate() {

        var review = easyRandom.nextObject(Review.class);

        when(reviewRepository.create(review)).thenReturn(review);

        var reviewCreated = reviewService.create(review);
        assertNotNull(reviewCreated);
        assertEquals(review, reviewCreated);

        verify(reviewRepository, times(1)).create(review);
        verify(modelBinder, times(1)).bindFields(review);
    }

    @Test
    void shouldReturnReviewWithFillingCreatedAtDateWhenCreate() {

        var review = easyRandom.nextObject(Review.class);
        review.setCreatedAt(null);

        when(reviewRepository.create(review)).thenAnswer(ans -> {
            var reviewParam = ans.getArgument(0, Review.class);
            reviewParam.setCreatedAt(LocalDateTime.now());
            return reviewParam;
        });

        var reviewCreated = reviewService.create(review);
        assertNotNull(reviewCreated);
        assertNotNull(reviewCreated.getCreatedAt());

        verify(reviewRepository, times(1)).create(review);
        verify(modelBinder, times(1)).bindFields(review);
    }

    @Test
    void shouldReturnReviewWhenUpdate() {

        var review = easyRandom.nextObject(Review.class);
        var id = review.getId();

        when(reviewRepository.update(id, review)).thenReturn(review);

        var reviewUpdated = reviewService.update(id, review);
        assertNotNull(reviewUpdated);
        assertEquals(review, reviewUpdated);

        verify(reviewRepository, times(1)).update(id, review);
        verify(modelBinder, times(1)).bindFields(review);
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var review = easyRandom.nextObject(Review.class);
        var id = "1";

        when(reviewRepository.update(id, review)).thenReturn(null);

        var reviewUpdated = reviewService.update(id, review);
        assertNull(reviewUpdated);

        verify(reviewRepository, times(1)).update(id, review);
        verify(modelBinder, times(1)).bindFields(null);
    }

    @Test
    void shouldDeleteReviewWhenDeleteById() {
        var id = "987";
        reviewService.deleteById(id);
        verify(reviewRepository, times(1)).deleteById(id);
    }
}