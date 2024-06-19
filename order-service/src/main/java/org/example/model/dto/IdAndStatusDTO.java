package org.example.model.dto;

import lombok.*;
import org.example.model.Status;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class IdAndStatusDTO {
    private String id;
    private Status status;
}
