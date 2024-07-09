package org.example.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public interface PersistedModel<T> {

    T getUniqueIdentifierForBindingWithOtherServices();

    static <T> Set<T> getUniqueIdentifiersOfCollectionOfModels(Collection<? extends PersistedModel<?>> models, Class<T> type) {
        return models.stream()
                .map(PersistedModel::getUniqueIdentifierForBindingWithOtherServices)
                .map(type::cast)
                .collect(Collectors.toSet());
    }
}
