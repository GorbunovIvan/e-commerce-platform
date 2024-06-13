package org.example.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
@Slf4j
public class ProductAndRatingInfo {

    private Long productId;
    private List<Review> reviews = new ArrayList<>();

    public Double getRating() {
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(-1D);
    }

    public Integer getNumberOfReviews() {
        return reviews.size();
    }

    public static List<ProductAndRatingInfo> reviewsToProductAndRatings(@NotNull List<Review> reviews) {
        log.info("Converting reviews to ProductAndRatingInfo");
        var map = reviews.stream()
                .collect(Collectors.groupingBy(Review::getProductId));
        return map.entrySet().stream()
                .map(entry -> new ProductAndRatingInfo(entry.getKey(), entry.getValue()))
                .toList();
    }
}
