package org.example.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.example.model.Category;
import org.example.model.Product;
import org.example.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode
@ToString
public class ProductDTO {

    private String name;
    private String description;
    private String category;
    private Long userId;
    private LocalDateTime createdAt;

    @JsonIgnore
    public Category getCategoryObj() {
        if (this.category == null) {
            return null;
        }
        var categoryObj = new Category();
        categoryObj.setName(this.category);
        return categoryObj;
    }

    @JsonIgnore
    public User getUser() {
        if (this.userId == null) {
            return null;
        }
        return new User(this.userId);
    }

    public Product toProduct() {
        return new Product(null, name, description, getCategoryObj(), getUser(), createdAt);
    }
}
