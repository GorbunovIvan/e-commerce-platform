package org.example.model.reviews;

import lombok.*;
import org.example.model.products.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "product" })
@ToString
public class ProductAndRatingInfo {

    private Product product;
    private Double rating;
    private Integer numberOfReviews;

    @Setter(AccessLevel.NONE)
    private List<Review> reviews = new ArrayList<>();

    public ProductAndRatingInfo(Product product, List<Review> reviews) {
        this.product = product;
        this.setReviews(reviews);
    }

    public Long getProductId() {
        if (this.product == null) {
            return null;
        }
        return this.product.getId();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = new ArrayList<>(reviews);
        this.numberOfReviews = reviews.size();
        this.rating = reviews.stream()
                .filter(review -> review.getRating() != null)
                .mapToInt(Review::getRating)
                .average()
                .orElse(-1D);
    }

    public static List<ProductAndRatingInfo> reviewsToProductAndRatings(List<Review> reviews) {
        var mapByProduct = reviews.stream()
                .collect(Collectors.groupingBy(Review::getProduct));
        return mapByProduct.entrySet().stream()
                .map(entry -> new ProductAndRatingInfo(entry.getKey(), entry.getValue()))
                .toList();
    }
}
