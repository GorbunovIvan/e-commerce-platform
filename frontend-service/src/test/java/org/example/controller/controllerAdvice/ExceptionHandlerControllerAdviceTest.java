package org.example.controller.controllerAdvice;

import org.example.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExceptionHandlerControllerAdviceTest {

    @Autowired
    private ExceptionHandlerControllerAdvice exceptionHandlerControllerAdvice;

    private Model model;

    @BeforeEach
    void setUp() {
        this.model = new BindingAwareModelMap();
    }

    @Test
    void shouldReturnErrorTemplateWithExceptionMessageWhenHandleException() {

        var cause = new Throwable("test error cause");
        var exception = new Exception("test error", cause);

        var result = exceptionHandlerControllerAdvice.handleException(exception, model);

        assertEquals("error", result);

        assertTrue(model.containsAttribute("title"));
        assertInstanceOf(String.class, model.getAttribute("title"));
        assertFalse(String.valueOf(model.getAttribute("title")).isEmpty());
        assertTrue(String.valueOf(model.getAttribute("title")).contains(exception.getMessage()));

        assertTrue(model.containsAttribute("message"));
        assertInstanceOf(String.class, model.getAttribute("message"));
        assertEquals(exception.getMessage(), String.valueOf(model.getAttribute("message")));
    }

    @Test
    void shouldReturnErrorTemplateWithExceptionCauseWhenHandleException() {

        var cause = new Throwable("test error cause");
        var exception = new Exception("", cause);

        var result = exceptionHandlerControllerAdvice.handleException(exception, model);

        assertEquals("error", result);

        assertTrue(model.containsAttribute("title"));
        assertInstanceOf(String.class, model.getAttribute("title"));
        assertFalse(String.valueOf(model.getAttribute("title")).isEmpty());
        assertTrue(String.valueOf(model.getAttribute("title")).contains(exception.getCause().getMessage()));

        assertTrue(model.containsAttribute("message"));
        assertInstanceOf(String.class, model.getAttribute("message"));
        assertEquals(exception.getCause().getMessage(), String.valueOf(model.getAttribute("message")));
    }

    @Test
    void shouldReturnErrorTemplateWithEmptyMessageWhenHandleException() {

        var exception = new Exception("");

        var result = exceptionHandlerControllerAdvice.handleException(exception, model);

        assertEquals("error", result);

        assertTrue(model.containsAttribute("title"));
        assertInstanceOf(String.class, model.getAttribute("title"));

        assertTrue(model.containsAttribute("message"));
        assertInstanceOf(String.class, model.getAttribute("message"));
        assertTrue(String.valueOf(model.getAttribute("message")).isEmpty());
    }

    @Test
    void shouldReturnErrorTemplateWhenHandleNotFoundException() {

        var exception = new NotFoundException("Test entity not found");

        var result = exceptionHandlerControllerAdvice.handleNotFoundException(exception, model);

        assertEquals("error", result);

        assertTrue(model.containsAttribute("title"));
        assertInstanceOf(String.class, model.getAttribute("title"));
        assertFalse(String.valueOf(model.getAttribute("title")).isEmpty());
        assertEquals(exception.getTitle(), String.valueOf(model.getAttribute("title")));

        assertTrue(model.containsAttribute("message"));
        assertInstanceOf(String.class, model.getAttribute("message"));
        assertEquals(exception.getMessage(), String.valueOf(model.getAttribute("message")));
    }
}