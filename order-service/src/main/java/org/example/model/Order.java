package org.example.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Document(collection = "orders")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Order {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;

    private Long userId;
    private Long productId;

    @EqualsAndHashCode.Exclude
    private LocalDateTime createdAt;

    @Transient
    @EqualsAndHashCode.Exclude
    private Status status;

    public Order(Long userId, Long productId, LocalDateTime createdAt) {
        this.userId = userId;
        this.productId = productId;
        this.createdAt = createdAt;
    }

    @EqualsAndHashCode.Include
    public LocalDateTime getCreatedAt() {
        if (this.createdAt == null) {
            return null;
        }
        return this.createdAt.truncatedTo(ChronoUnit.SECONDS);
    }
}
