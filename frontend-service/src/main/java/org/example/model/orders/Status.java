package org.example.model.orders;

public enum Status {

    CREATED,
    IN_PROGRESS,
    IN_A_WAY,
    DELIVERED,
    DELETED;

    public static Status statusByName(String name) {
        try {
            return Status.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Status nextStatusAfter(Status status) {
        if (status == null) {
            return Status.CREATED;
        }
        var spotted = false;
        for (var statusToCheck : Status.values()) {
            if (spotted) {
                return statusToCheck;
            }
            if (statusToCheck.equals(status)) {
                spotted = true;
            }
        }
        return null;
    }
}
