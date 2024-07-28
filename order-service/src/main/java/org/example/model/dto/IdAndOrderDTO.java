package org.example.model.dto;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class IdAndOrderDTO {
    private String id;
    private OrderDTO orderDTO;
}
