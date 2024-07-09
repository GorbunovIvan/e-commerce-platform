package org.example.service.modelsBinding;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.PersistedModel;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModelBinder {

    private final ModelsSearcher modelsSearcher;

    public <T> T bindFields(T entity) {

        if (entity == null) {
            return null;
        }

        if (entity instanceof Collection<?> collection) {
            bindFieldsInEachCollectionEntity(collection);
        } else {
            bindFieldsInEntity(entity);
        }

        return entity;
    }


    // Binding necessary fields of provided entity
    private <T> void bindFieldsInEntity(@NotNull T entity) {

        var fields = findFieldsToBind(entity.getClass());

        for (var field : fields) {

            log.info("Binding field '{}' of entity {} to model by reference", field.getName(), entity);

            var isAccessible = field.canAccess(entity);
            try {
                if (!isAccessible) {
                    field.trySetAccessible();
                }
                bindField(entity, field);
            } catch (Exception e) {
                log.error("Failed to bind model to field '{}' - {}", field.getName(), e.getMessage());
            } finally {
                if (!isAccessible) {
                    field.setAccessible(false);
                }
            }
        }
    }

    private <T> void bindField(T entity, Field field) throws IllegalAccessException {

        var valueOfField = field.get(entity);

        if (valueOfField instanceof PersistedModel<?> model) {

            var modelFoundByReference = modelsSearcher.findObjectByReference(model);
            if (modelFoundByReference != null) {
                field.set(entity, modelFoundByReference);
            } else {
                log.warn("No entity found by reference '{}' - {}", model.getUniqueIdentifierForBindingWithOtherServices(), model);
            }

        } else if (valueOfField instanceof Collection<?> collectionOfModels) {
            bindFieldsInEachCollectionEntity(collectionOfModels, true);
        }
    }


    // Binding necessary fields of each entity of provided collection
    private void bindFieldsInEachCollectionEntity(Collection<?> collection) {
        bindFieldsInEachCollectionEntity(collection, false);
    }

    private void bindFieldsInEachCollectionEntity(Collection<?> collection, boolean bindCollectionElementsThemselvesToo) {

        if (collection.isEmpty()) {
            return;
        }

        if (bindCollectionElementsThemselvesToo) {
            bindCollectionOfEntities(collection);
        }

        var firstElement = collection.iterator().next();
        var fields = findFieldsToBind(firstElement.getClass());
        for (var field : fields) {
            bindFieldInEachCollectionEntity(collection, field);
        }
    }

    private void bindCollectionOfEntities(Collection<?> collection) {

        var firstElement = collection.iterator().next();
        if (!(firstElement instanceof PersistedModel<?>)) {
            return;
        }

        @SuppressWarnings("unchecked")
        var collectionOfModels = (Collection<PersistedModel<?>>) collection;

        var modelsFoundByReferences = modelsSearcher.findObjectsByReferences(new ArrayList<>(collectionOfModels));

        if (modelsFoundByReferences.size() < collectionOfModels.size()) {
            log.warn("{} entities of type '{}' were not found by their references.\n init collection - {}\n collection to search from - {}",
                    collectionOfModels.size() - modelsFoundByReferences.size(),
                    firstElement.getClass(),
                    collectionOfModels,
                    modelsFoundByReferences);
        }

        collectionOfModels.clear();
        collectionOfModels.addAll(modelsFoundByReferences);
    }


    private void bindFieldInEachCollectionEntity(Collection<?> collection, Field field) {

        log.info("Binding field '{}' to models by their references in each entities from {}", field.getName(), collection);

        var isAccessible = field.isAccessible();

        try {
            if (!isAccessible) {
                field.trySetAccessible();
            }
            bindFieldInEachEntityWithFoundEntityAnalogue(collection, field);
        } catch (Exception e) {
            log.error("Failed to bind models of field '{}' in collection entities - {}", field.getName(), e.getMessage());
        } finally {
            if (!isAccessible) {
                field.setAccessible(false);
            }
        }
    }

    private void bindFieldInEachEntityWithFoundEntityAnalogue(Collection<?> collection, Field field) throws IllegalAccessException {

        var modelsToBind = new ArrayList<PersistedModel<?>>();

        // First we need to fetch all the models
        // from specified field of each entity of provided collection
        for (var entity : collection) {
            var valueOfField = field.get(entity);
            if (valueOfField instanceof PersistedModel<?> model) {
                modelsToBind.add(model);
            }
        }

        // Searching all the entities by their unique identifiers in one query
        var modelsFoundByReferences = modelsSearcher.findObjectsByReferences(modelsToBind);

        // Matching each entity with its found analogue by their unique identifiers
        for (var entity : collection) {
            var valueOfField = field.get(entity);
            if (valueOfField instanceof PersistedModel<?> model) {
                var modelFoundByUniqueIdentifier = findModelInCollectionByUniqueIdentifier(model, modelsFoundByReferences);
                if (modelFoundByUniqueIdentifier != null) {
                    field.set(entity, modelFoundByUniqueIdentifier);
                }
            }
        }
    }

    private <T> PersistedModel<?> findModelInCollectionByUniqueIdentifier(PersistedModel<?> model, Collection<? extends PersistedModel<?>> collection) {

        var uniqueIdentifierOfModel = model.getUniqueIdentifierForBindingWithOtherServices();

        var modelFoundByUniqueIdentifier = collection.stream()
                .filter(modelPotential -> Objects.equals(uniqueIdentifierOfModel, modelPotential.getUniqueIdentifierForBindingWithOtherServices()))
                .findAny()
                .orElse(null);

        if (modelFoundByUniqueIdentifier == null) {
            log.warn("No entity found by reference '{}' - {}", uniqueIdentifierOfModel, model);
        }

        return modelFoundByUniqueIdentifier;
    }


    private List<Field> findFieldsToBind(Class<?> clazz) {

        if (clazz == null) {
            return Collections.emptyList();
        }

        // Recursion to add all fields of superclasses
        var resultFields = new ArrayList<>(findFieldsToBind(clazz.getSuperclass()));

        var foundFields = Arrays.stream(clazz.getDeclaredFields())
                // We only need fields whose type is a child of a PersistedModel or is a collection of fields.
                .filter(field -> PersistedModel.class.isAssignableFrom(field.getType())
                              || Collection.class.isAssignableFrom(field.getType()))
                .toList();

        resultFields.addAll(foundFields);

        return resultFields;
    }
}
