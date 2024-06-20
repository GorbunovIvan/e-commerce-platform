package org.example.model.orders;

import lombok.*;
import org.example.model.products.Product;
import org.example.model.users.User;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Order {
    private String id;
    private User user;
    private Product product;
    private LocalDateTime createdAt;
    private Status status;
}
