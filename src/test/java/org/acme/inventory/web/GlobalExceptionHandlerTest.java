package org.acme.inventory.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import org.acme.inventory.exception.ResourceNotFoundException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void htmlAcceptReturnsErrorView() {
        Object result = handler.handleNotFound(notFound(), request("text/html,application/xhtml+xml"));
        ModelAndView view = assertInstanceOf(ModelAndView.class, result);
        assertEquals("error", view.getViewName());
        assertEquals(HttpStatus.NOT_FOUND, view.getStatus());
        ProblemDetail problem = assertInstanceOf(ProblemDetail.class, view.getModel().get("problem"));
        assertEquals("Resource not found", problem.getTitle());
    }

    @Test
    void jsonAcceptReturnsProblemDetail() {
        Object result = handler.handleNotFound(notFound(), request("application/json"));
        ProblemDetail problem = assertInstanceOf(ProblemDetail.class, result);
        assertEquals(404, problem.getStatus());
        assertEquals("Resource not found", problem.getTitle());
    }

    @Test
    void wildcardAcceptStaysJson() {
        assertFalse(GlobalExceptionHandler.wantsHtml(request("*/*")));
        assertTrue(GlobalExceptionHandler.wantsHtml(request("text/html")));
    }

    private static ResourceNotFoundException notFound() {
        return new ResourceNotFoundException("Product", UUID.fromString("7c9e6679-7425-40de-944b-e07fc1f90ae7"));
    }

    private static ServletWebRequest request(String accept) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.ACCEPT, accept);
        return new ServletWebRequest(request);
    }
}
