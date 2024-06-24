package org.example.model.reviews;

import lombok.*;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "product", "user", "createdAt" })
@ToString
public class Review {

    private String id;
    private Product product;
    private User user;
    private Integer rating;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createdAt;

    public Long getUserId() {
        if (this.user != null) {
            return this.user.getId();
        }
        return null;
    }

    public String getUserUsername() {
        if (this.user != null) {
            return this.user.getUsername();
        }
        return "";
    }

    public String getUserView() {
        if (this.user != null) {
            return this.user.getUniqueView();
        }
        return "";
    }

    public Long getProductId() {
        if (this.product != null) {
            return this.product.getId();
        }
        return null;
    }

    public String getProductName() {
        if (this.product != null) {
            return this.product.getName();
        }
        return "";
    }

    public String getProductView() {
        if (this.product != null) {
            return this.product.getUniqueView();
        }
        return "";
    }

    public String shortInfo() {

        var productView = getProductName();
        var userView = getUserUsername();
        var createdAtView = getCreatedAt();
        var ratingView = getRating();

        return String.format("%s by %s at %s - rating: %d",
                productView.isEmpty() ? "<no-product>" : productView,
                userView.isEmpty() ? "<no-user>" : userView,
                createdAtView == null ? "<no-created-at>" : createdAtView.truncatedTo(ChronoUnit.SECONDS),
                ratingView);
    }
}
