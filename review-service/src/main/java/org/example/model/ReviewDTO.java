package org.example.model;

import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class ReviewDTO {

    private Long productId;
    private Long userId;
    private Integer rating;

    @EqualsAndHashCode.Exclude
    private LocalDateTime createdAt;

    @EqualsAndHashCode.Include
    public LocalDateTime getCreatedAt() {
        if (this.createdAt == null) {
            return null;
        }
        return this.createdAt.truncatedTo(ChronoUnit.SECONDS);
    }

    public Review toReview() {
        return new Review(null,
                this.getProductId(),
                this.getUserId(),
                this.getRating(),
                this.getCreatedAt());
    }
}
