package org.example.model.orders;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class StatusTrackerRecord {
    private String id;
    private Order order;
    private String status;
    private LocalDateTime time;
}
