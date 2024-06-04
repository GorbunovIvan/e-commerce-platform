package org.example.model.dto;

import lombok.*;
import org.example.model.Order;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class OrderDTO {

    private Long userId;
    private Long productId;
    private LocalDateTime createdAt;

    public Order toOrder() {
        return new Order(userId, productId, createdAt);
    }
}
