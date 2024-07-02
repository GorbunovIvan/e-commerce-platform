package org.example.model.orders.dto;

import lombok.*;
import org.example.model.orders.Order;
import org.example.model.products.Product;
import org.example.model.users.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
public class OrderRequestDTO {

    private Long userId;
    private Long productId;
    private LocalDateTime createdAt;

    public User getUser() {
        var user = new User();
        user.setId(this.userId);
        return user;
    }

    public Product getProduct() {
        var product = new Product();
        product.setId(this.productId);
        return product;
    }

    public Order toOrder() {
        var order = new Order();
        order.setUser(getUser());
        order.setProduct(getProduct());
        order.setCreatedAt(getCreatedAt());
        return order;
    }

    public static List<Order> toOrders(Collection<OrderRequestDTO> ordersDTO) {
        return ordersDTO.stream()
                .map(OrderRequestDTO::toOrder)
                .toList();
    }

    public static OrderRequestDTO fromOrder(Order order) {
        if (order == null) {
            return null;
        }
        var orderDTO = new OrderRequestDTO();
        orderDTO.setUserId(order.getUserId());
        orderDTO.setProductId(order.getProductId());
        orderDTO.setCreatedAt(order.getCreatedAt());
        return orderDTO;
    }

    public static List<OrderRequestDTO> fromOrders(Collection<Order> orders) {
        return orders.stream()
                .map(OrderRequestDTO::fromOrder)
                .toList();
    }
}
