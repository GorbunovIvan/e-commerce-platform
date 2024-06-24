package org.example.model.orders;

import lombok.*;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "user", "product", "createdAt" })
@ToString
public class Order implements Comparable<Order> {

    private String id;
    private User user;
    private Product product;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createdAt;

    private Status status;

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
        var statusView = getStatus();
        var createdAtView = getCreatedAt();

        return String.format("%s by %s (%s) - %s",
                productView.isEmpty() ? "<no-product>" : productView,
                userView.isEmpty() ? "<no-user>" : userView,
                statusView == null ? "<no-status>" : statusView,
                createdAtView == null ? "<no-time>" : createdAtView.truncatedTo(ChronoUnit.SECONDS));
    }

    @Override
    public int compareTo(Order o) {
        if (o.getCreatedAt() == null) {
            return 1;
        }
        if (getCreatedAt() == null) {
            return -1;
        }
        return getCreatedAt().compareTo(o.getCreatedAt());
    }
}
