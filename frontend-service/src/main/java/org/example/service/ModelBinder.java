package org.example.service;

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

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModelBinder {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final StatusTrackerRecordRepository statusTrackerRecordRepository;
    private final ReviewRepository reviewRepository;

    public <T> T bindFields(T entity) {

        if (entity == null) {
            return null;
        }

        if (entity instanceof Collection<?> collection) {
            //noinspection unchecked
            return (T) bindFieldsOfEntitiesOfCollection(collection);
        }

        var fields = getAllFields(entity.getClass());

        for (var field : fields) {

            log.info("Binding field '{}' of entity {} to model by reference", field.getName(), entity);

            var isAccessible = field.canAccess(entity);
            try {
                if (!isAccessible) {
                    field.trySetAccessible();
                }
                var valueOfField = field.get(entity);
                if (valueOfField instanceof PersistedModel<?> model) {
                    var modelFound = getObjectByReference(model);
                    if (modelFound == null) {
                        var uniqueValue = model.getUniqueValueToBindEntitiesFromRemoteServices();
                        log.warn("No entity found by reference '{}' - {}", uniqueValue, valueOfField);
                    }
                    field.set(entity, modelFound);
                }
            } catch (Exception e) {
                log.error("Failed to bind model to field '{}' - {}", field.getName(), e.getMessage());
            } finally {
                if (!isAccessible) {
                    field.setAccessible(false);
                }
            }
        }

        return entity;
    }

    private Collection<?> bindFieldsOfEntitiesOfCollection(Collection<?> collection) {

        if (collection.isEmpty()) {
            return collection;
        }

        var element = collection.iterator().next();
        var fields = getAllFields(element.getClass());

        for (var field : fields) {

            log.info("Binding field '{}' of entities {} to model by reference", field.getName(), collection);

            var isAccessible = field.isAccessible();
            if (!isAccessible) {
                field.trySetAccessible();
            }

            try {

                var modelsToBind = new ArrayList<PersistedModel<?>>();

                // Gathering all the models from each entity of provided collection from current (loop above) field
                for (var entity : collection) {
                    var value = field.get(entity);
                    if (value instanceof PersistedModel<?> model) {
                        modelsToBind.add(model);
                    }
                }

                var modelsFound = getObjectsByReferences(modelsToBind);

                for (var entity : collection) {
                    var valueOfField = field.get(entity);
                    if (valueOfField instanceof PersistedModel<?> model) {

                        var uniqueValue = model.getUniqueValueToBindEntitiesFromRemoteServices();

                        var modelFound = modelsFound.stream()
                                .filter(m -> Objects.equals(uniqueValue, m.getUniqueValueToBindEntitiesFromRemoteServices()))
                                .findAny()
                                .orElse(null);

                        if (modelFound == null) {
                            log.warn("No entity found by reference '{}' - {}", uniqueValue, valueOfField);
                        }

                        field.set(entity, modelFound);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to bind models of field '{}' - {}", field.getName(), e.getMessage());
            } finally {
                if (!isAccessible) {
                    field.setAccessible(false);
                }
            }
        }

        return collection;
    }

    private PersistedModel<?> getObjectByReference(@NonNull PersistedModel<?> model) {

        if (model instanceof User user) {
            return userRepository.getById(user.getUniqueValueToBindEntitiesFromRemoteServices());
        } else if (model instanceof Product product) {
            return productRepository.getById(product.getUniqueValueToBindEntitiesFromRemoteServices());
        } else if (model instanceof Category category) {
            return productRepository.getCategoryByName(category.getUniqueValueToBindEntitiesFromRemoteServices());
        } else if (model instanceof Order order) {
            return orderRepository.getById(order.getUniqueValueToBindEntitiesFromRemoteServices());
        } else if (model instanceof StatusTrackerRecord statusTrackerRecord) {
            return statusTrackerRecordRepository.getById(statusTrackerRecord.getUniqueValueToBindEntitiesFromRemoteServices());
        } else if (model instanceof Review review) {
            return reviewRepository.getById(review.getUniqueValueToBindEntitiesFromRemoteServices());
        } else {
            var errorMessage = String.format("Unknown model as a field type: %s", model.getClass());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    @SuppressWarnings("unchecked")
    private List<? extends PersistedModel<?>> getObjectsByReferences(List<? extends PersistedModel<?>> models) {

        if (models.isEmpty()) {
            return models;
        }

        var firstModel = models.getFirst();

        if (firstModel instanceof User) {
            var ids = (Set<Long>) getCollectionOfModelsUniqueValues(models);
            return userRepository.getByIds(ids);
        } else if (firstModel instanceof Product) {
            var ids = (Set<Long>) getCollectionOfModelsUniqueValues(models);
            return productRepository.getByIds(ids);
        } else if (firstModel instanceof Category) {
            var names = (Set<String>) getCollectionOfModelsUniqueValues(models);
            return productRepository.getCategoriesByNames(names);
        } else if (firstModel instanceof Order) {
            var ids = (Set<String>) getCollectionOfModelsUniqueValues(models);
            return orderRepository.getByIds(ids);
        } else if (firstModel instanceof StatusTrackerRecord) {
            var ids = (Set<String>) getCollectionOfModelsUniqueValues(models);
            return statusTrackerRecordRepository.getByIds(ids);
        } else if (firstModel instanceof Review) {
            var ids = (Set<String>) getCollectionOfModelsUniqueValues(models);
            return reviewRepository.getByIds(ids);
        } else {
            var errorMessage = String.format("Unknown model as a field type: %s", firstModel.getClass());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    private List<Field> getAllFields(Class<?> clazz) {

        if (clazz == null) {
            return Collections.emptyList();
        }

        // Recursion to add all fields of superclasses
        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));

        List<Field> filteredFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> PersistedModel.class.isAssignableFrom(field.getType())) // We only need fields whose type is a child of PersistedModel
                .toList();
        result.addAll(filteredFields);

        return result;
    }

    private Set<?> getCollectionOfModelsUniqueValues(List<? extends PersistedModel<?>> models) {
        return models.stream()
                .map(PersistedModel::getUniqueValueToBindEntitiesFromRemoteServices)
                .collect(Collectors.toSet());
    }
}
