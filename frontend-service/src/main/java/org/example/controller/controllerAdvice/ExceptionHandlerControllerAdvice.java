package org.example.controller.controllerAdvice;

import org.example.exception.NotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler
    String handleException(Exception e, Model model) {
        var message = getMessageFromException(e);
        model.addAttribute("title", "Error: " + message.substring(0, Math.min(20, message.length())));
        model.addAttribute("message", message);
        return "error";
    }

    @ExceptionHandler(NotFoundException.class)
    String handleNotFoundException(NotFoundException e, Model model) {
        var message = getMessageFromException(e);
        model.addAttribute("title", e.getTitle());
        model.addAttribute("message", message);
        return "error";
    }

    private String getMessageFromException(Exception e) {
        var message = e.getMessage();
        if (message != null && !message.isEmpty()) {
            return message;
        }
        var cause = e.getCause();
        if (cause != null) {
            var causeMessage = cause.getMessage();
            if (causeMessage != null && !causeMessage.isEmpty()) {
                return causeMessage;
            }
            return cause.toString();
        }
        return "";
    }
}
