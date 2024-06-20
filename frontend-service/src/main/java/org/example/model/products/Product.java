package org.example.model.products;

import lombok.*;
import org.example.model.users.User;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Product {
    private Long id;
    private String name;
    private String description;
    private Category category;
    private User user;
    private LocalDateTime createdAt;
}
