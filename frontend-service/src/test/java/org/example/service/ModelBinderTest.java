package org.example.service;

import org.example.model.orders.Order;
import org.example.model.orders.StatusTrackerRecord;
import org.example.model.products.Category;
import org.example.model.products.Product;
import org.example.model.reviews.ProductAndRatingInfo;
import org.example.model.reviews.Review;
import org.example.model.users.User;
import org.example.repository.orders.OrderRepository;
import org.example.repository.orders.StatusTrackerRecordRepository;
import org.example.repository.products.ProductRepository;
import org.example.repository.reviews.ReviewRepository;
import org.example.repository.users.UserRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@SpringBootTest
class ModelBinderTest {

    @Autowired
    private ModelBinder modelBinder;

    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private StatusTrackerRecordRepository statusTrackerRecordRepository;
    @MockBean
    private ReviewRepository reviewRepository;

    private final EasyRandom easyRandom = new EasyRandom();

    // Users
    @Test
    void shouldBindFieldsOfUserWhenBindFields() {

        var user = easyRandom.nextObject(User.class);
        var userExpected = copyUser(user);

        modelBinder.bindFields(user);
        assertEquals(userExpected, user);
    }

    @Test
    void shouldBindFieldsOfCollectionsOfUsersWhenBindFields() {

        var users = easyRandom.objects(User.class, 5).collect(Collectors.toSet());

        var usersExpected = new HashSet<User>();
        for (var user : users) {
            var userCopy = copyUser(user);
            usersExpected.add(userCopy);
        }

        modelBinder.bindFields(users);
        assertEquals(usersExpected, users);
    }

    // Products
    @Test
    void shouldBindFieldsOfProductWhenBindFields() {

        var userExisting = easyRandom.nextObject(User.class);
        var categoryExisting = easyRandom.nextObject(Category.class);

        var user = new User();
        user.setId(userExisting.getId());

        var category = new Category();
        category.setName(categoryExisting.getName());

        var product = easyRandom.nextObject(Product.class);
        product.setUser(user);
        product.setCategory(category);

        var productExpected = copyProduct(product);
        productExpected.setUser(userExisting);
        productExpected.setCategory(categoryExisting);

        when(userRepository.getById(user.getId())).thenReturn(userExisting);
        when(productRepository.getCategoryByName(category.getName())).thenReturn(categoryExisting);

        modelBinder.bindFields(product);
        assertEquals(productExpected, product);
        assertNotNull(product.getUserUsername());
        assertEquals(userExisting, product.getUser());
        assertNotNull(product.getCategoryName());
        assertEquals(categoryExisting, product.getCategory());

        verify(userRepository, times(1)).getById(user.getId());
        verify(productRepository, times(1)).getCategoryByName(category.getName());
    }

    @Test
    void shouldBindFieldsOfCollectionOfProductsWhenBindFields() {

        var products = new HashSet<Product>();
        var productsExpected = new HashSet<Product>();

        var numberOfProducts = 6;

        var usersExisting = easyRandom.objects(User.class, numberOfProducts/2).toList();
        var categoriesExisting = easyRandom.objects(Category.class, numberOfProducts/2).toList();

        for (int i = 0; i < numberOfProducts; i++) {

            var userExisting = usersExisting.get(i/2);
            var categoryExisting = categoriesExisting.get(i/2);

            var user = new User();
            user.setId(userExisting.getId());

            var category = new Category();
            category.setName(categoryExisting.getName());

            var product = easyRandom.nextObject(Product.class);
            product.setUser(user);
            product.setCategory(category);

            products.add(product);

            var productExpected = copyProduct(product);
            productExpected.setUser(userExisting);
            productExpected.setCategory(categoryExisting);

            productsExpected.add(productExpected);
        }

        var usersIds = usersExisting.stream().map(User::getId).collect(Collectors.toSet());
        var categoriesNames = categoriesExisting.stream().map(Category::getName).collect(Collectors.toSet());

        when(userRepository.getByIds(usersIds)).thenReturn(usersExisting);
        when(productRepository.getCategoriesByNames(categoriesNames)).thenReturn(categoriesExisting);

        modelBinder.bindFields(products);

        assertEquals(productsExpected, products);

        for (var product : products) {
            assertNotNull(product.getUserUsername());
            assertNotNull(product.getCategoryName());
        }

        verify(userRepository, times(1)).getByIds(usersIds);
        verify(productRepository, times(1)).getCategoriesByNames(categoriesNames);
        verify(userRepository, never()).getById(anyLong());
        verify(productRepository, never()).getCategoryByName(anyString());
    }

    // Orders
    @Test
    void shouldBindFieldsOfOrderWhenBindFields() {

        var userExisting = easyRandom.nextObject(User.class);
        var productExisting = easyRandom.nextObject(Product.class);

        var user = new User();
        user.setId(userExisting.getId());

        var product = new Product();
        product.setId(productExisting.getId());

        var order = easyRandom.nextObject(Order.class);
        order.setUser(user);
        order.setProduct(product);

        var orderExpected = copyOrder(order);
        orderExpected.setUser(userExisting);
        orderExpected.setProduct(productExisting);

        when(userRepository.getById(user.getId())).thenReturn(userExisting);
        when(productRepository.getById(product.getId())).thenReturn(productExisting);

        modelBinder.bindFields(order);
        assertEquals(orderExpected, order);
        assertNotNull(order.getUserUsername());
        assertEquals(userExisting, order.getUser());
        assertNotNull(order.getProductName());
        assertEquals(productExisting, order.getProduct());

        verify(userRepository, times(1)).getById(user.getId());
        verify(productRepository, times(1)).getById(product.getId());
    }

    @Test
    void shouldBindFieldsOfCollectionOfOrdersWhenBindFields() {

        var orders = new HashSet<Order>();
        var ordersExpected = new HashSet<Order>();

        var numberOfOrders = 6;

        var usersExisting = easyRandom.objects(User.class, numberOfOrders/2).toList();
        var productsExisting = easyRandom.objects(Product.class, numberOfOrders/2).toList();

        for (int i = 0; i < numberOfOrders; i++) {

            var userExisting = usersExisting.get(i/2);
            var productExisting = productsExisting.get(i/2);

            var user = new User();
            user.setId(userExisting.getId());

            var product = new Product();
            product.setId(productExisting.getId());

            var order = easyRandom.nextObject(Order.class);
            order.setUser(user);
            order.setProduct(product);

            orders.add(order);

            var orderExpected = copyOrder(order);
            orderExpected.setUser(userExisting);
            orderExpected.setProduct(productExisting);

            ordersExpected.add(orderExpected);
        }

        var usersIds = usersExisting.stream().map(User::getId).collect(Collectors.toSet());
        var productsIds = productsExisting.stream().map(Product::getId).collect(Collectors.toSet());

        when(userRepository.getByIds(usersIds)).thenReturn(usersExisting);
        when(productRepository.getByIds(productsIds)).thenReturn(productsExisting);

        modelBinder.bindFields(orders);

        assertEquals(ordersExpected, orders);

        for (var order : orders) {
            assertNotNull(order.getUserUsername());
            assertNotNull(order.getProductName());
        }

        verify(userRepository, times(1)).getByIds(usersIds);
        verify(productRepository, times(1)).getByIds(productsIds);
        verify(userRepository, never()).getById(anyLong());
        verify(productRepository, never()).getById(anyLong());
    }

    // StatusTrackerRecords
    @Test
    void shouldBindFieldsOfStatusTrackerRecordWhenBindFields() {

        var orderExisting = easyRandom.nextObject(Order.class);

        var order = new Order();
        order.setId(orderExisting.getId());

        var statusRecord = easyRandom.nextObject(StatusTrackerRecord.class);
        statusRecord.setOrder(order);

        var statusRecordExpected = copyStatusTrackerRecord(statusRecord);
        statusRecordExpected.setOrder(orderExisting);

        when(orderRepository.getById(order.getId())).thenReturn(orderExisting);

        modelBinder.bindFields(statusRecord);
        assertEquals(statusRecordExpected, statusRecord);
        assertNotNull(statusRecord.getOrder().getCreatedAt());
        assertEquals(orderExisting, statusRecord.getOrder());

        verify(orderRepository, times(1)).getById(order.getId());
    }

    @Test
    void shouldBindFieldsOfCollectionOfStatusTrackerRecordsWhenBindFields() {

        var statusRecords = new HashSet<StatusTrackerRecord>();
        var statusRecordsExpected = new HashSet<StatusTrackerRecord>();

        var numberOfStatusRecords = 6;

        var ordersExisting = easyRandom.objects(Order.class, numberOfStatusRecords/2).toList();

        for (int i = 0; i < numberOfStatusRecords; i++) {

            var orderExisting = ordersExisting.get(i/2);

            var order = new Order();
            order.setId(orderExisting.getId());

            var statusRecord = easyRandom.nextObject(StatusTrackerRecord.class);
            statusRecord.setOrder(order);

            statusRecords.add(statusRecord);

            var statusRecordExpected = copyStatusTrackerRecord(statusRecord);
            statusRecordExpected.setOrder(orderExisting);

            statusRecordsExpected.add(statusRecordExpected);
        }

        var ordersIds = ordersExisting.stream().map(Order::getId).collect(Collectors.toSet());

        when(orderRepository.getByIds(ordersIds)).thenReturn(ordersExisting);

        modelBinder.bindFields(statusRecords);

        assertEquals(statusRecordsExpected, statusRecords);

        for (var statusRecord : statusRecords) {
            assertNotNull(statusRecord.getOrder().getCreatedAt());
        }

        verify(orderRepository, times(1)).getByIds(ordersIds);
        verify(orderRepository, never()).getById(anyString());
    }

    // Reviews
    @Test
    void shouldBindFieldsOfReviewWhenBindFields() {

        var userExisting = easyRandom.nextObject(User.class);
        var productExisting = easyRandom.nextObject(Product.class);

        var user = new User();
        user.setId(userExisting.getId());

        var product = new Product();
        product.setId(productExisting.getId());

        var review = easyRandom.nextObject(Review.class);
        review.setUser(user);
        review.setProduct(product);

        var reviewExpected = copyReview(review);
        reviewExpected.setUser(userExisting);
        reviewExpected.setProduct(productExisting);

        when(userRepository.getById(user.getId())).thenReturn(userExisting);
        when(productRepository.getById(product.getId())).thenReturn(productExisting);

        modelBinder.bindFields(review);
        assertEquals(reviewExpected, review);
        assertNotNull(review.getUserUsername());
        assertEquals(userExisting, review.getUser());
        assertNotNull(review.getProductName());
        assertEquals(productExisting, review.getProduct());

        verify(userRepository, times(1)).getById(user.getId());
        verify(productRepository, times(1)).getById(product.getId());
    }

    @Test
    void shouldBindFieldsOfCollectionOfReviewsWhenBindFields() {

        var reviews = new HashSet<Review>();
        var reviewsExpected = new HashSet<Review>();

        var numberOfReviews = 6;

        var usersExisting = easyRandom.objects(User.class, numberOfReviews/2).toList();
        var productsExisting = easyRandom.objects(Product.class, numberOfReviews/2).toList();

        for (int i = 0; i < numberOfReviews; i++) {

            var userExisting = usersExisting.get(i/2);
            var productExisting = productsExisting.get(i/2);

            var user = new User();
            user.setId(userExisting.getId());

            var product = new Product();
            product.setId(productExisting.getId());

            var review = easyRandom.nextObject(Review.class);
            review.setUser(user);
            review.setProduct(product);

            reviews.add(review);

            var reviewExpected = copyReview(review);
            reviewExpected.setUser(userExisting);
            reviewExpected.setProduct(productExisting);

            reviewsExpected.add(reviewExpected);
        }

        var usersIds = usersExisting.stream().map(User::getId).collect(Collectors.toSet());
        var productsIds = productsExisting.stream().map(Product::getId).collect(Collectors.toSet());

        when(userRepository.getByIds(usersIds)).thenReturn(usersExisting);
        when(productRepository.getByIds(productsIds)).thenReturn(productsExisting);

        modelBinder.bindFields(reviews);

        assertEquals(reviewsExpected, reviews);

        for (var review : reviews) {
            assertNotNull(review.getUserUsername());
            assertNotNull(review.getProductName());
        }

        verify(userRepository, times(1)).getByIds(usersIds);
        verify(productRepository, times(1)).getByIds(productsIds);
        verify(userRepository, never()).getById(anyLong());
        verify(productRepository, never()).getById(anyLong());
    }

    // ProductAndRatingInfo
    @Test
    void shouldBindFieldsOfProductAndRatingInfoWhenBindFields() {

        var productExisting = easyRandom.nextObject(Product.class);
        var reviewsExisting = easyRandom.objects(Review.class, 5).toList();
        reviewsExisting.forEach(review -> review.setProduct(productExisting));

        var product = new Product();
        product.setId(productExisting.getId());

        var reviews = reviewsExisting.stream()
                .map(r -> {
                    var review = new Review();
                    review.setId(r.getId());
                    return review;
                })
                .toList();

        var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
        productAndRatingInfo.setProduct(product);
        productAndRatingInfo.setReviews(reviews);

        var productAndRatingInfoExpected = copyProductAndRatingInfo(productAndRatingInfo);
        productAndRatingInfoExpected.setProduct(productExisting);
        productAndRatingInfoExpected.setReviews(reviewsExisting);

        var reviewsIds = reviewsExisting.stream().map(Review::getId).collect(Collectors.toSet());

        when(productRepository.getById(product.getId())).thenReturn(productExisting);
        when(reviewRepository.getByIds(reviewsIds)).thenReturn(reviewsExisting);

        modelBinder.bindFields(productAndRatingInfo);
        assertEquals(productAndRatingInfoExpected, productAndRatingInfo);
        assertNotNull(productAndRatingInfo.getProduct().getName());
        assertEquals(productExisting, productAndRatingInfo.getProduct());
        assertNotNull(productAndRatingInfo.getReviews());
        assertFalse(productAndRatingInfo.getReviews().isEmpty());
        assertEquals(reviewsExisting, productAndRatingInfo.getReviews());

        verify(productRepository, times(1)).getById(product.getId());
        verify(reviewRepository, times(1)).getByIds(reviewsIds);
        verify(reviewRepository, never()).getById(anyString());
    }

    @Test
    void shouldBindFieldsOfCollectionOfProductAndRatingInfosWhenBindFields() {

        var productAndRatingInfoCollection = new ArrayList<ProductAndRatingInfo>();
        var productAndRatingInfoCollectionExpected = new ArrayList<ProductAndRatingInfo>();

        var numberOfProducts = 5;

        var productsExisting = easyRandom.objects(Product.class, numberOfProducts).toList();
        var reviewsExisting = new ArrayList<Review>();
        for (var product : productsExisting) {
            var reviewsExistingOfProduct = easyRandom.objects(Review.class, 5).toList();
            reviewsExistingOfProduct.forEach(review -> review.setProduct(product));
            reviewsExisting.addAll(reviewsExistingOfProduct);
        }

        for (int i = 0; i < numberOfProducts; i++) {

            var productExisting = productsExisting.get(i);
            var reviewsExistingByProduct = reviewsExisting.stream()
                    .filter(review -> review.getProduct().equals(productExisting))
                    .toList();

            var product = new Product();
            product.setId(productExisting.getId());

            var reviews = reviewsExistingByProduct.stream()
                    .map(r -> {
                        var review = new Review();
                        review.setId(r.getId());
                        return review;
                    })
                    .toList();

            var productAndRatingInfo = easyRandom.nextObject(ProductAndRatingInfo.class);
            productAndRatingInfo.setProduct(product);
            productAndRatingInfo.setReviews(reviews);

            productAndRatingInfoCollection.add(productAndRatingInfo);

            var productAndRatingInfoExpected = copyProductAndRatingInfo(productAndRatingInfo);
            productAndRatingInfoExpected.setProduct(productExisting);
            productAndRatingInfoExpected.setReviews(reviewsExistingByProduct);

            productAndRatingInfoCollectionExpected.add(productAndRatingInfoExpected);
        }

        var productsIds = productsExisting.stream().map(Product::getId).collect(Collectors.toSet());
        var reviewsIds = reviewsExisting.stream().map(Review::getId).collect(Collectors.toSet());

        when(productRepository.getByIds(productsIds)).thenReturn(productsExisting);

        modelBinder.bindFields(productAndRatingInfoCollection);

        assertEquals(new HashSet<>(productAndRatingInfoCollectionExpected), new HashSet<>(productAndRatingInfoCollection));

        for (var productAndRatingInfo : productAndRatingInfoCollection) {

            assertNotNull(productAndRatingInfo.getProduct().getName());
            assertNotNull(productAndRatingInfo.getReviews());
            assertFalse(productAndRatingInfo.getReviews().isEmpty());

            var reviewsIdsByProductExpected = reviewsExisting.stream()
                    .filter(review -> review.getProduct().equals(productAndRatingInfo.getProduct()))
                    .map(Review::getId)
                    .collect(Collectors.toSet());

            var reviewsIdsByProduct = productAndRatingInfo.getReviews().stream()
                    .map(Review::getId)
                    .collect(Collectors.toSet());

            assertEquals(reviewsIdsByProductExpected, reviewsIdsByProduct);
        }

        verify(productRepository, times(1)).getByIds(productsIds);
        verify(productRepository, never()).getById(anyLong());
        verify(reviewRepository, never()).getByIds(reviewsIds);
        verify(reviewRepository, never()).getById(anyString());
    }

    private User copyUser(User user) {
        return new User(user.getId(), user.getUsername());
    }

    private Product copyProduct(Product product) {
        return new Product(
                product.getId(),
                product.getName(),
                product.getDescription(),
                copyCategory(product.getCategory()),
                copyUser(product.getUser()),
                product.getCreatedAt());
    }

    private Category copyCategory(Category category) {
        return new Category(category.getId(), category.getName());
    }

    private Order copyOrder(Order order) {
        return new Order(order.getId(), order.getUser(), order.getProduct(), order.getCreatedAt(), order.getStatus());
    }

    private StatusTrackerRecord copyStatusTrackerRecord(StatusTrackerRecord statusRecord) {
        return new StatusTrackerRecord(statusRecord.getId(), statusRecord.getOrder(), statusRecord.getStatus(), statusRecord.getTime());
    }

    private Review copyReview(Review review) {
        return new Review(review.getId(), review.getProduct(), review.getUser(), review.getRating(), review.getCreatedAt());
    }

    private ProductAndRatingInfo copyProductAndRatingInfo(ProductAndRatingInfo productAndRatingInfo) {
        var reviews = productAndRatingInfo.getReviews().stream()
                .map(this::copyReview)
                .toList();
        return new ProductAndRatingInfo(productAndRatingInfo.getProduct(), reviews);
    }
}