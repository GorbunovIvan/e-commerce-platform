package org.example.model.users;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class User {
    private Long id;
    private String username;
    public String password;
}
