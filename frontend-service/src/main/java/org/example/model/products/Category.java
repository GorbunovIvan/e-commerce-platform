package org.example.model.products;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "name" })
@ToString
public class Category {
    private Integer id;
    private String name;
}
