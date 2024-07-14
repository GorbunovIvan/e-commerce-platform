package org.example.repository.orders.remote;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.orders.Order;
import org.example.model.orders.dto.OrderDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
@ConditionalOnProperty(name = "order-service.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(name = "httpGraphQlClient")
@RequiredArgsConstructor
@Slf4j
public class OrderRepositoryGraphQL {

    private final HttpGraphQlClient httpGraphQlClient;

    public Order getById(String id) {

        log.info("Searching for order with id={}", id);

        String query = """
                {
                  getOrderById(id: "%s") {
                      id
                      userId
                      productId
                      createdAt
                      status
                  }
                }
                """;

        query = String.format(query, id);

        var responseFuture = httpGraphQlClient.document(query)
                .retrieve("getOrderById")
                .toEntity(OrderDTO.class)
                .toFuture();

        try {
            var orderDTO = responseFuture.get();
            if (orderDTO == null) {
                return null;
            }
            return orderDTO.toOrder();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get order by id - {}", e.getMessage());
            return null;
        }
    }

    public List<Order> getByIds(Set<String> ids) {

        log.info("Searching for orders with ids={}", ids);

        String query = """
                {
                  getOrdersByIds(ids: ["%s"]) {
                      id
                      userId
                      productId
                      createdAt
                      status
                  }
                }
                """;

        var idsAsParam = String.join("\", \"", ids);
        query = String.format(query, idsAsParam);

        var responseFuture = httpGraphQlClient.document(query)
                .retrieve("getOrdersByIds")
                .toEntityList(OrderDTO.class)
                .toFuture();

        try {
            var ordersDTO = responseFuture.get();
            return OrderDTO.toOrders(ordersDTO);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get orders by ids - {}", e.getMessage());
            return null;
        }
    }

    public List<Order> getAll() {

        log.info("Searching for all orders");

        String query = """
                {
                  getAllOrders {
                      id
                      userId
                      productId
                      createdAt
                      status
                  }
                }
                """;

        var responseFuture = httpGraphQlClient.document(query)
                .retrieve("getAllOrders")
                .toEntityList(OrderDTO.class)
                .toFuture();

        try {
            var ordersDTO = responseFuture.get();
            return OrderDTO.toOrders(ordersDTO);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get orders - {}", e.getMessage());
            return null;
        }
    }

    public List<Order> getAllByUser(Long userId) {

        log.info("Searching for orders with userId={}", userId);

        String query = """
                {
                  getAllOrdersByUser(userId: "%s") {
                      id
                      userId
                      productId
                      createdAt
                      status
                  }
                }
                """;

        query = String.format(query, userId);

        var responseFuture = httpGraphQlClient.document(query)
                .retrieve("getAllOrdersByUser")
                .toEntityList(OrderDTO.class)
                .toFuture();

        try {
            var ordersDTO = responseFuture.get();
            return OrderDTO.toOrders(ordersDTO);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get orders by userId - {}", e.getMessage());
            return null;
        }
    }

    public List<Order> getAllByProduct(Long productId) {

        log.info("Searching for orders with productId={}", productId);

        String query = """
                {
                  getAllOrdersByProduct(productId: "%s") {
                      id
                      userId
                      productId
                      createdAt
                      status
                  }
                }
                """;

        query = String.format(query, productId);

        var responseFuture = httpGraphQlClient.document(query)
                .retrieve("getAllOrdersByProduct")
                .toEntityList(OrderDTO.class)
                .toFuture();

        try {
            var ordersDTO = responseFuture.get();
            return OrderDTO.toOrders(ordersDTO);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get orders by productId - {}", e.getMessage());
            return null;
        }
    }
}
