package org.example.model;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
public class UserDTO {

    public String username;
    public LocalDate createdAt;

    public UserDTO(String username) {
        this.username = username;
    }

    public User toUser() {
        var user = new User();
        user.setUsername(username);
        user.setCreatedAt(createdAt);
        return user;
    }
}
