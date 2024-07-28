package org.example.model.orders.dto;

import lombok.*;
import org.example.model.orders.Status;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class IdAndStatusDTO {
    private String id;
    private Status status;
}
