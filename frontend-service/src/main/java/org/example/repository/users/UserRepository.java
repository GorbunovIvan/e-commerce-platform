package org.example.repository.users;

import org.example.model.users.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserRepository {
    List<User> getAll();
    User getById(Long id);
    User getByUsername(String username);
    User create(User user);
    User update(Long id, User user);
    void deleteById(Long id);
}
