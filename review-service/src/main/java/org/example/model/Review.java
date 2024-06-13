package org.example.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Document(collection = "reviews")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Review {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;

    @NotNull
    private Long productId;

    @NotNull
    private Long userId;

    @NotNull
    @Size(max = 10)
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
}
