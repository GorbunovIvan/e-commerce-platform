package org.example.model.reviews;

import lombok.*;
import org.example.model.products.Product;
import org.example.model.users.User;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Review {
    private String id;
    private Product product;
    private User user;
    private Integer rating;
    private LocalDateTime createdAt;
}
