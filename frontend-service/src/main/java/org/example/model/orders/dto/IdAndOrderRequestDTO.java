package org.example.model.orders.dto;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class IdAndOrderRequestDTO {
    private String id;
    private OrderRequestDTO orderDTO;
}
