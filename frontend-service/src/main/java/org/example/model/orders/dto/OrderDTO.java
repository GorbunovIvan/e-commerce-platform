package org.example.model.orders.dto;

import lombok.*;
import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.products.Product;
import org.example.model.users.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
public class OrderDTO {

    private String id;
    private Long userId;
    private Long productId;

    @EqualsAndHashCode.Exclude
    private Status status;

    @EqualsAndHashCode.Exclude
    private LocalDateTime createdAt;

    @EqualsAndHashCode.Include
    public LocalDateTime getCreatedAt() {
        if (this.createdAt == null) {
            return null;
        }
        return this.createdAt.truncatedTo(ChronoUnit.SECONDS);
    }

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
        order.setId(getId());
        order.setUser(getUser());
        order.setProduct(getProduct());
        order.setStatus(getStatus());
        order.setCreatedAt(getCreatedAt());
        return order;
    }

    public static List<Order> toOrders(Collection<OrderDTO> ordersDTO) {
        return ordersDTO.stream()
                .map(OrderDTO::toOrder)
                .toList();
    }

    public static OrderDTO fromOrder(Order order) {
        if (order == null) {
            return null;
        }
        var orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setUserId(order.getUserId());
        orderDTO.setProductId(order.getProductId());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCreatedAt(order.getCreatedAt());
        return orderDTO;
    }

    public static List<OrderDTO> fromOrders(Collection<Order> orders) {
        return orders.stream()
                .map(OrderDTO::fromOrder)
                .toList();
    }
}
