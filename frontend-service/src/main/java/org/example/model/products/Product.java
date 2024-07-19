package org.example.model.products;

import lombok.*;
import org.example.model.PersistedModel;
import org.example.model.users.User;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "name", "category", "user", "createdAt" })
@ToString
public class Product implements PersistedModel<Long> {

    private Long id;
    private String name;
    private String description;
    private Category category;
    private User user;
    private LocalDateTime createdAt;

    public String getCategoryName() {
        if (this.category != null) {
            return this.category.getName();
        }
        return "";
    }

    public void setCategoryName(String categoryName) {
        this.category = new Category();
        this.category.setName(categoryName);
    }

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

    public String getUniqueView() {
        if (getId() == null && getName() == null) {
            return "";
        }
        return String.format("%s (id=%s)", getName(), getId());
    }

    public static Pattern patternToReadIdFromUniqueView() {
        return Pattern.compile(".*\\(id=(\\d+)\\)$");
    }

    @Override
    public Long getUniqueIdentifierForBindingWithOtherServices() {
        return getId();
    }
}
