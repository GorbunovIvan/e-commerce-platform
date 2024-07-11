package org.example.service.modelsBinding;

import org.example.model.PersistedModel;
import org.example.model.orders.Order;
import org.example.model.orders.StatusTrackerRecord;
import org.example.model.products.Category;
import org.example.model.products.Product;
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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ModelsSearcherTest {

    @Autowired
    private ModelsSearcher modelsSearcher;

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

    // Miscellaneous cases
    @Test
    void shouldThrowExceptionWhenFindObjectByReference() {

        var model = new PersistedModel<Integer>() {
            @Override
            public Integer getUniqueIdentifierForBindingWithOtherServices() {
                return 0;
            }
        };

        assertThrows(RuntimeException.class, () -> modelsSearcher.findObjectByReference(model));
    }

    @Test
    void shouldReturnEmptyCollectionWhenFindObjectsByReferences() {
        var models = modelsSearcher.findObjectsByReferences(Collections.emptyList());
        assertNotNull(models);
        assertTrue(models.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenFindObjectsByReferences() {

        var model = new PersistedModel<Integer>() {
            @Override
            public Integer getUniqueIdentifierForBindingWithOtherServices() {
                return 0;
            }
        };

        var models = new ArrayList<PersistedModel<?>>();
        models.add(model);

        assertThrows(RuntimeException.class, () -> modelsSearcher.findObjectsByReferences(models));
    }

    // Users
    @Test
    void shouldReturnUserWhenFindObjectByReference() {

        var userExisting = easyRandom.nextObject(User.class);

        var user = easyRandom.nextObject(User.class);
        user.setId(userExisting.getId());

        var userUniqueIdentifier = user.getUniqueIdentifierForBindingWithOtherServices();

        when(userRepository.getById(userUniqueIdentifier)).thenReturn(userExisting);

        var userFound = modelsSearcher.findObjectByReference(user);
        assertNotNull(userFound);
        assertEquals(userExisting, userFound);

        verify(userRepository, times(1)).getById(userUniqueIdentifier);
    }

    @Test
    void shouldReturnUsersWhenFindObjectsByReferences() {

        var usersExisting = easyRandom.objects(User.class, 5).toList();

        var users = new ArrayList<User>();
        for (var userExisting : usersExisting) {
            var user = easyRandom.nextObject(User.class);
            user.setId(userExisting.getId());
            users.add(user);
        }

        var usersUniqueIdentifiers = PersistedModel.getUniqueIdentifiersOfCollectionOfModels(users, Long.class);

        when(userRepository.getByIds(usersUniqueIdentifiers)).thenReturn(usersExisting);

        var usersFound = modelsSearcher.findObjectsByReferences(users);
        assertNotNull(usersFound);
        assertFalse(usersFound.isEmpty());
        assertEquals(usersExisting, usersFound);

        verify(userRepository, times(1)).getByIds(usersUniqueIdentifiers);
    }

    // Products
    @Test
    void shouldReturnProductWhenFindObjectByReference() {

        var productExisting = easyRandom.nextObject(Product.class);

        var product = easyRandom.nextObject(Product.class);
        product.setId(productExisting.getId());

        var productUniqueIdentifier = product.getUniqueIdentifierForBindingWithOtherServices();

        when(productRepository.getById(productUniqueIdentifier)).thenReturn(productExisting);

        var productFound = modelsSearcher.findObjectByReference(product);
        assertNotNull(productFound);
        assertEquals(productExisting, productFound);

        verify(productRepository, times(1)).getById(productUniqueIdentifier);
    }

    @Test
    void shouldReturnProductsWhenFindObjectsByReferences() {

        var productsExisting = easyRandom.objects(Product.class, 5).toList();

        var products = new ArrayList<Product>();
        for (var productExisting : productsExisting) {
            var product = easyRandom.nextObject(Product.class);
            product.setId(productExisting.getId());
            products.add(product);
        }

        var productsUniqueIdentifiers = PersistedModel.getUniqueIdentifiersOfCollectionOfModels(products, Long.class);

        when(productRepository.getByIds(productsUniqueIdentifiers)).thenReturn(productsExisting);

        var productsFound = modelsSearcher.findObjectsByReferences(products);
        assertNotNull(productsFound);
        assertFalse(productsFound.isEmpty());
        assertEquals(productsExisting, productsFound);

        verify(productRepository, times(1)).getByIds(productsUniqueIdentifiers);
    }

    // Categories
    @Test
    void shouldReturnCategoryWhenFindObjectByReference() {

        var categoryExisting = easyRandom.nextObject(Category.class);

        var category = easyRandom.nextObject(Category.class);
        category.setId(categoryExisting.getId());

        var categoryUniqueIdentifier = category.getUniqueIdentifierForBindingWithOtherServices();

        when(productRepository.getCategoryByName(categoryUniqueIdentifier)).thenReturn(categoryExisting);

        var categoryFound = modelsSearcher.findObjectByReference(category);
        assertNotNull(categoryFound);
        assertEquals(categoryExisting, categoryFound);

        verify(productRepository, times(1)).getCategoryByName(categoryUniqueIdentifier);
    }

    @Test
    void shouldReturnCategoriesWhenFindObjectsByReferences() {

        var categoriesExisting = easyRandom.objects(Category.class, 5).toList();

        var categories = new ArrayList<Category>();
        for (var categoryExisting : categoriesExisting) {
            var category = easyRandom.nextObject(Category.class);
            category.setId(categoryExisting.getId());
            categories.add(category);
        }

        var categoriesUniqueIdentifiers = PersistedModel.getUniqueIdentifiersOfCollectionOfModels(categories, String.class);

        when(productRepository.getCategoriesByNames(categoriesUniqueIdentifiers)).thenReturn(categoriesExisting);

        var categoriesFound = modelsSearcher.findObjectsByReferences(categories);
        assertNotNull(categoriesFound);
        assertFalse(categoriesFound.isEmpty());
        assertEquals(categoriesExisting, categoriesFound);

        verify(productRepository, times(1)).getCategoriesByNames(categoriesUniqueIdentifiers);
    }

    // Orders
    @Test
    void shouldReturnOrderWhenFindObjectByReference() {

        var orderExisting = easyRandom.nextObject(Order.class);

        var order = easyRandom.nextObject(Order.class);
        order.setId(orderExisting.getId());

        var orderUniqueIdentifier = order.getUniqueIdentifierForBindingWithOtherServices();

        when(orderRepository.getById(orderUniqueIdentifier)).thenReturn(orderExisting);

        var orderFound = modelsSearcher.findObjectByReference(order);
        assertNotNull(orderFound);
        assertEquals(orderExisting, orderFound);

        verify(orderRepository, times(1)).getById(orderUniqueIdentifier);
    }

    @Test
    void shouldReturnOrdersWhenFindObjectsByReferences() {

        var ordersExisting = easyRandom.objects(Order.class, 5).toList();

        var orders = new ArrayList<Order>();
        for (var orderExisting : ordersExisting) {
            var order = easyRandom.nextObject(Order.class);
            order.setId(orderExisting.getId());
            orders.add(order);
        }

        var ordersUniqueIdentifiers = PersistedModel.getUniqueIdentifiersOfCollectionOfModels(orders, String.class);

        when(orderRepository.getByIds(ordersUniqueIdentifiers)).thenReturn(ordersExisting);

        var ordersFound = modelsSearcher.findObjectsByReferences(orders);
        assertNotNull(ordersFound);
        assertFalse(ordersFound.isEmpty());
        assertEquals(ordersExisting, ordersFound);

        verify(orderRepository, times(1)).getByIds(ordersUniqueIdentifiers);
    }

    // StatusTrackerRecords
    @Test
    void shouldReturnStatusTrackerRecordWhenFindObjectByReference() {

        var statusTrackerRecordExisting = easyRandom.nextObject(StatusTrackerRecord.class);

        var statusTrackerRecord = easyRandom.nextObject(StatusTrackerRecord.class);
        statusTrackerRecord.setId(statusTrackerRecordExisting.getId());

        var statusTrackerRecordUniqueIdentifier = statusTrackerRecord.getUniqueIdentifierForBindingWithOtherServices();

        when(statusTrackerRecordRepository.getById(statusTrackerRecordUniqueIdentifier)).thenReturn(statusTrackerRecordExisting);

        var statusTrackerRecordFound = modelsSearcher.findObjectByReference(statusTrackerRecord);
        assertNotNull(statusTrackerRecordFound);
        assertEquals(statusTrackerRecordExisting, statusTrackerRecordFound);

        verify(statusTrackerRecordRepository, times(1)).getById(statusTrackerRecordUniqueIdentifier);
    }

    @Test
    void shouldReturnStatusTrackerRecordsWhenFindObjectsByReferences() {

        var statusTrackerRecordsExisting = easyRandom.objects(StatusTrackerRecord.class, 5).toList();

        var statusTrackerRecords = new ArrayList<StatusTrackerRecord>();
        for (var statusTrackerRecordExisting : statusTrackerRecordsExisting) {
            var statusTrackerRecord = easyRandom.nextObject(StatusTrackerRecord.class);
            statusTrackerRecord.setId(statusTrackerRecordExisting.getId());
            statusTrackerRecords.add(statusTrackerRecord);
        }

        var statusTrackerRecordsUniqueIdentifiers = PersistedModel.getUniqueIdentifiersOfCollectionOfModels(statusTrackerRecords, String.class);

        when(statusTrackerRecordRepository.getByIds(statusTrackerRecordsUniqueIdentifiers)).thenReturn(statusTrackerRecordsExisting);

        var statusTrackerRecordsFound = modelsSearcher.findObjectsByReferences(statusTrackerRecords);
        assertNotNull(statusTrackerRecordsFound);
        assertFalse(statusTrackerRecordsFound.isEmpty());
        assertEquals(statusTrackerRecordsExisting, statusTrackerRecordsFound);

        verify(statusTrackerRecordRepository, times(1)).getByIds(statusTrackerRecordsUniqueIdentifiers);
    }

    // Reviews
    @Test
    void shouldReturnReviewWhenFindObjectByReference() {

        var reviewExisting = easyRandom.nextObject(Review.class);

        var review = easyRandom.nextObject(Review.class);
        review.setId(reviewExisting.getId());

        var reviewUniqueIdentifier = review.getUniqueIdentifierForBindingWithOtherServices();

        when(reviewRepository.getById(reviewUniqueIdentifier)).thenReturn(reviewExisting);

        var reviewFound = modelsSearcher.findObjectByReference(review);
        assertNotNull(reviewFound);
        assertEquals(reviewExisting, reviewFound);

        verify(reviewRepository, times(1)).getById(reviewUniqueIdentifier);
    }

    @Test
    void shouldReturnReviewsWhenFindObjectsByReferences() {

        var reviewsExisting = easyRandom.objects(Review.class, 5).toList();

        var reviews = new ArrayList<Review>();
        for (var reviewExisting : reviewsExisting) {
            var review = easyRandom.nextObject(Review.class);
            review.setId(reviewExisting.getId());
            reviews.add(review);
        }

        var reviewsUniqueIdentifiers = PersistedModel.getUniqueIdentifiersOfCollectionOfModels(reviews, String.class);

        when(reviewRepository.getByIds(reviewsUniqueIdentifiers)).thenReturn(reviewsExisting);

        var reviewsFound = modelsSearcher.findObjectsByReferences(reviews);
        assertNotNull(reviewsFound);
        assertFalse(reviewsFound.isEmpty());
        assertEquals(reviewsExisting, reviewsFound);

        verify(reviewRepository, times(1)).getByIds(reviewsUniqueIdentifiers);
    }
}