package org.example.model.orders;

import lombok.*;
import org.example.model.PersistedModel;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "order", "status", "time" })
@ToString
public class StatusTrackerRecord implements PersistedModel<String>, Comparable<StatusTrackerRecord> {

    private String id;
    private Order order;
    private Status status;
    private LocalDateTime time;

    public String shortInfo() {
        return shortInfo(true);
    }

    public String shortInfo(boolean addOrderInfo) {

        String orderView = null;
        if (addOrderInfo) {
            orderView = getOrder() == null ? "" : getOrder().shortInfo();
        }
        var statusView = getStatus() == null ? "" : getStatus().name();
        var timeView = getTime();

        if (addOrderInfo) {
            return String.format("%s  -  %s  at %s",
                    orderView.isEmpty() ? "<no-order>" : orderView,
                    statusView.isEmpty() ? "<no-status>" : statusView,
                    timeView == null ? "<no-time>" : timeView.truncatedTo(ChronoUnit.SECONDS));
        } else {
            return String.format("%s  at %s",
                    statusView.isEmpty() ? "<no-status>" : statusView,
                    timeView == null ? "<no-time>" : timeView.truncatedTo(ChronoUnit.SECONDS));
        }
    }

    @Override
    public int compareTo(StatusTrackerRecord o) {
        if (o.getTime() == null) {
            return 1;
        }
        if (getTime() == null) {
            return -1;
        }
        return getTime().compareTo(o.getTime());
    }

    @Override
    public String getUniqueIdentifierForBindingWithOtherServices() {
        return getId();
    }
}
