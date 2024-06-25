package org.example.controller.controllerAdvice;

import org.example.exception.NotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler
    private String handleException(Exception e, Model model) {
        var message = getMessageFromException(e);
        model.addAttribute("title", "Error: " + message.substring(0, 20));
        model.addAttribute("message", message);
        return "error";
    }

    @ExceptionHandler(NotFoundException.class)
    private String handleNotFoundExceptionException(NotFoundException e, Model model) {
        var message = getMessageFromException(e);
        model.addAttribute("title", e.getTitle());
        model.addAttribute("message", message);
        return "error";
    }

    private String getMessageFromException(Exception e) {
        var message = e.getMessage();
        if (message.isEmpty()) {
            message = e.getCause().toString();
        }
        return message;
    }
}
