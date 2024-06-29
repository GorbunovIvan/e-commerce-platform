package org.example.model.products;

import lombok.*;
import org.example.model.PersistedModel;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "name" })
@ToString
public class Category implements PersistedModel<String> {

    private Integer id;
    private String name;

    @Override
    public String getUniqueValueToBindEntitiesFromRemoteServices() {
        return getName();
    }
}
