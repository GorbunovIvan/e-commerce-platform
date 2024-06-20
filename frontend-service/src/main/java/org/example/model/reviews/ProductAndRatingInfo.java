package org.example.model.reviews;

import lombok.*;
import org.example.model.products.Product;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "product" })
@ToString
public class ProductAndRatingInfo {
    private Product product;
    private Double rating;
    private Integer numberOfReviews;
}
