package org.example.model;

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
}
