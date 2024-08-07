package org.example.repository;

import org.example.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findAllByIdIn(@Param("ids") Collection<String>ids);
    List<Order> findAllByProductId(@Param("productId") Long productId);
    List<Order> findAllByUserId(@Param("userId") Long userId);
}
