package org.acme.inventory.web;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;

import org.acme.inventory.exception.ResourceNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleNotFound(ResourceNotFoundException ex, NativeWebRequest request) {
        return respond(request, problem(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(IllegalArgumentException ex, NativeWebRequest request) {
        return respond(request, problem(HttpStatus.BAD_REQUEST, "Invalid request", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidation(MethodArgumentNotValidException ex, NativeWebRequest request) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return respond(request, problem(HttpStatus.BAD_REQUEST, "Validation failed", detail));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleConstraintViolation(ConstraintViolationException ex, NativeWebRequest request) {
        String detail = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
        return respond(request, problem(HttpStatus.BAD_REQUEST, "Validation failed", detail));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Object handleConflict(DataIntegrityViolationException ex, NativeWebRequest request) {
        return respond(request, problem(HttpStatus.CONFLICT, "Conflict", "Request violates a data constraint"));
    }

    private static Object respond(NativeWebRequest request, ProblemDetail body) {
        if (wantsHtml(request)) {
            return new ModelAndView("error", Map.of("problem", body), HttpStatus.valueOf(body.getStatus()));
        }
        return body;
    }

    static boolean wantsHtml(NativeWebRequest request) {
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        if (accept == null || accept.isBlank()) {
            return false;
        }
        List<MediaType> accepted = MediaType.parseMediaTypes(accept);
        return accepted.stream()
                .filter(mediaType -> !mediaType.isWildcardType() && !mediaType.isWildcardSubtype())
                .anyMatch(mediaType -> mediaType.isCompatibleWith(MediaType.TEXT_HTML));
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }
}
