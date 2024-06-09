package org.example.model.dto;

import lombok.*;
import org.example.model.Order;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class OrderDTO {

    private Long userId;
    private Long productId;

    @EqualsAndHashCode.Exclude
    private LocalDateTime createdAt;

    public Order toOrder() {
        return new Order(userId, productId, createdAt);
    }

    @EqualsAndHashCode.Include
    public LocalDateTime getCreatedAt() {
        if (this.createdAt == null) {
            return null;
        }
        return this.createdAt.truncatedTo(ChronoUnit.SECONDS);
    }
}
