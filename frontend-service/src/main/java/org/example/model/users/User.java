package org.example.model.users;

import lombok.*;
import org.example.model.PersistedModel;

import java.util.regex.Pattern;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "username" })
@ToString
public class User implements PersistedModel<Long> {

    private Long id;
    private String username;

    public String getUniqueView() {
        if (getId() == null && getUsername() == null) {
            return "";
        }
        return String.format("%s (id=%s)", getUsername(), getId());
    }

    public static Pattern patternToReadIdFromUniqueView() {
        return Pattern.compile(".*\\(id=(\\d+)\\)$");
    }

    @Override
    public Long getUniqueIdentifierForBindingWithOtherServices() {
        return getId();
    }
}
