package com.timtrix.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<Map<String, String>>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult()
                                              .getFieldErrors()
                                              .stream()
                                              .map(fieldError -> Map.of(
                                                  "field", fieldError.getField(),
                                                  "message", Objects.requireNonNull(fieldError.getDefaultMessage())))
                                              .toList();
        return new ResponseEntity<>(Map.of("errors", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, List<Map<String, String>>>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        List<Map<String, String>> errors = List.of(Map.of(
                "field", "email",
                "message", ex.getMessage()
        ));
        return new ResponseEntity<>(Map.of("errors", errors), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        Map<String, Object> errorResponse = Map.of(
                "status", "Bad request",
                "message", ex.getMessage(),
                "statusCode", 401
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, List<Map<String, String>>>> handleGeneralException(Exception ex) {
        List<Map<String, String>> errors = List.of(Map.of(
                "field", "general",
                "message", ex.getMessage()
        ));
        return new ResponseEntity<>(Map.of("errors", errors), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
