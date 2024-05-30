package org.example.controller.controllerAdvice;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<String> handleException(ConstraintViolationException e) {
        var message = e.getConstraintViolations().stream()
                .map(c -> c.getPropertyPath() + " - " + c.getMessage())
                .collect(Collectors.joining("/n"));
        return new ResponseEntity<>(message, HttpStatusCode.valueOf(400));
    }
}
