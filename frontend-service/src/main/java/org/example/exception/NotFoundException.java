package org.example.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final String title = "Not found";

    public NotFoundException(String message) {
        super(message);
    }
}
