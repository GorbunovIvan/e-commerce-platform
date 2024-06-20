package org.example.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.products.Category;
import org.example.model.products.Product;
import org.example.model.reviews.Review;
import org.example.model.users.User;
import org.example.repository.orders.OrderRepositoryDummy;
import org.example.repository.orders.StatusTrackerRecordRepositoryDummy;
import org.example.repository.products.ProductRepositoryDummy;
import org.example.repository.reviews.ReviewRepositoryDummy;
import org.example.repository.users.UserRepositoryDummy;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@ConditionalOnProperty(name = "dummy-repositories.enable", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartupRunnerDummyRepositories implements ApplicationRunner {

    private final UserRepositoryDummy userRepository;
    private final ProductRepositoryDummy productRepository;
    private final ReviewRepositoryDummy reviewRepository;
    private final OrderRepositoryDummy orderRepository;
    private final StatusTrackerRecordRepositoryDummy statusTrackerRecordRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("\n===\nFilling dummy repositories with dummy data\n===");
        fillUserRepository();
        fillProductRepository();
        fillReviewRepository();
        fillOrderRepository();
        fillStatusTrackerRecordRepository();
        log.info("\n===\nDummy repositories are filled with dummy data\n===");
    }

    private void fillUserRepository() {
        userRepository.create(new User(null, "username-1", "password-1"));
        userRepository.create(new User(null, "username-2", "password-2"));
        userRepository.create(new User(null, "username-3", "password-3"));
        userRepository.create(new User(null, "username-4", "password-4"));
    }

    private void fillProductRepository() {

        var categories = List.of(
                new Category(1, "category-1"),
                new Category(2, "category-2"),
                new Category(3, "category-3")
        );

        var users = userRepository.getAll();

        productRepository.create(new Product(null, "name-1", "description-1", categories.get(0), users.get(3), LocalDateTime.now()));
        productRepository.create(new Product(null, "name-2", "description-2", categories.get(1), users.get(2), LocalDateTime.now()));
        productRepository.create(new Product(null, "name-3", "description-3", categories.get(2), users.get(1), LocalDateTime.now()));
        productRepository.create(new Product(null, "name-4", "description-4", categories.get(1), users.get(2), LocalDateTime.now()));
        productRepository.create(new Product(null, "name-5", "description-5", categories.get(0), users.get(3), LocalDateTime.now()));
        productRepository.create(new Product(null, "name-6", "description-6", categories.get(2), users.get(1), LocalDateTime.now()));
        productRepository.create(new Product(null, "name-7", "description-7", categories.get(0), users.get(2), LocalDateTime.now()));
    }

    private void fillReviewRepository() {

        var users = userRepository.getAll();
        var products = productRepository.getAll(null, null, null);

        var random = new Random();

        for (var product : products) {
            for (var user : users) {
                var rating = random.nextInt(10) + 1;
                reviewRepository.create(new Review(null, product, user, rating, LocalDateTime.now()));
            }
        }
    }

    private void fillOrderRepository() {

        var users = userRepository.getAll();
        var products = productRepository.getAll(null, null, null);

        for (var product : products) {
            for (var user : users) {
                orderRepository.create(new Order(null, user, product, LocalDateTime.now(), null));
            }
        }
    }

    private void fillStatusTrackerRecordRepository() {

        var orders = orderRepository.getAll();

        var random = new Random();

        for (var order : orders) {
            for (int i = 0; i < 4; i++) {
                var statusIndex = random.nextInt(Status.values().length);
                var status = Status.values()[statusIndex];
                orderRepository.changeOrderStatus(order.getId(), status);
            }
        }
    }
}
