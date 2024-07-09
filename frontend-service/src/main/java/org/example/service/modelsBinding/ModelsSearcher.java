package org.example.service.modelsBinding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Searching for models in other services by their unique identifiers
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ModelsSearcher {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final StatusTrackerRecordRepository statusTrackerRecordRepository;
    private final ReviewRepository reviewRepository;

    public PersistedModel<?> findObjectByReference(@NonNull PersistedModel<?> model) {
        if (model instanceof User user) {
            return userRepository.getById(user.getUniqueIdentifierForBindingWithOtherServices());
        } else if (model instanceof Product product) {
            return productRepository.getById(product.getUniqueIdentifierForBindingWithOtherServices());
        } else if (model instanceof Category category) {
            return productRepository.getCategoryByName(category.getUniqueIdentifierForBindingWithOtherServices());
        } else if (model instanceof Order order) {
            return orderRepository.getById(order.getUniqueIdentifierForBindingWithOtherServices());
        } else if (model instanceof StatusTrackerRecord statusTrackerRecord) {
            return statusTrackerRecordRepository.getById(statusTrackerRecord.getUniqueIdentifierForBindingWithOtherServices());
        } else if (model instanceof Review review) {
            return reviewRepository.getById(review.getUniqueIdentifierForBindingWithOtherServices());
        } else {
            var errorMessage = String.format("Unable to bind entity of type '%s' - unknown model", model.getClass());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    public Collection<? extends PersistedModel<?>> findObjectsByReferences(Collection<? extends PersistedModel<?>> models) {

        if (models.isEmpty()) {
            return models;
        }

        var firstModel = models.iterator().next();

        if (firstModel instanceof User) {
            var ids = PersistedModel.getUniqueIdentifiersOfCollectionOfModels(models, Long.class);
            return userRepository.getByIds(ids);
        } else if (firstModel instanceof Product) {
            var ids = PersistedModel.getUniqueIdentifiersOfCollectionOfModels(models, Long.class);
            return productRepository.getByIds(ids);
        } else if (firstModel instanceof Category) {
            var names = PersistedModel.getUniqueIdentifiersOfCollectionOfModels(models, String.class);
            return productRepository.getCategoriesByNames(names);
        } else if (firstModel instanceof Order) {
            var ids = PersistedModel.getUniqueIdentifiersOfCollectionOfModels(models, String.class);
            return orderRepository.getByIds(ids);
        } else if (firstModel instanceof StatusTrackerRecord) {
            var ids = PersistedModel.getUniqueIdentifiersOfCollectionOfModels(models, String.class);
            return statusTrackerRecordRepository.getByIds(ids);
        } else if (firstModel instanceof Review) {
            var ids = PersistedModel.getUniqueIdentifiersOfCollectionOfModels(models, String.class);
            return reviewRepository.getByIds(ids);
        } else {
            var errorMessage = String.format("Unable to bind entities of type '%s' - unknown model", firstModel.getClass());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
