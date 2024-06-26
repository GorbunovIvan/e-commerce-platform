package org.example.repository.users.remote;

import org.example.model.users.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${user-service.name}", url = "${user-service.url}")
@ConditionalOnProperty(name = "user-service.enabled", havingValue = "true", matchIfMissing = true)
public interface UserServiceFeignClient {

    @GetMapping
    ResponseEntity<List<User>> getAll();

    @GetMapping("/{id}")
    ResponseEntity<User> getById(@PathVariable Long id);

    @GetMapping("/username/{username}")
    ResponseEntity<User> getByUsername(@PathVariable String username);

    @PostMapping
    ResponseEntity<User> create(@RequestBody User userDTO);

    @PutMapping("/{id}")
    ResponseEntity<User> update(@PathVariable Long id, @RequestBody User userDTO);

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteById(@PathVariable Long id);
}
