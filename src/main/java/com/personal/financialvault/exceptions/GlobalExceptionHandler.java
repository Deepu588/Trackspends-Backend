package com.personal.financialvault.exceptions;

import com.personal.financialvault.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(
            MethodArgumentNotValidException ex) {
        String error = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(error)
                        .build());
    }

@ExceptionHandler(BadCredentialsException.class)
public ResponseEntity<?> handleInvalidCredentials(BadCredentialsException ex){
        return ResponseEntity.status(401).body(Map.of("success",false,"message","Invalid Credentials"));
}

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(
            IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("Something went wrong. Please try again.")
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleTypeError(HttpMessageNotReadableException ex) {

        String field = "unknown";
        String expectedType = "unknown";

        Throwable cause = ex.getMostSpecificCause();

        if (cause instanceof InvalidFormatException formatEx) {
            field = formatEx.getPath().get(0).getPropertyName();
                    //getFieldName();
            expectedType = formatEx.getTargetType().getSimpleName();
        }

        return ResponseEntity.badRequest().body(
                Map.of(
                        //"status", 400,
                        "message", "Invalid datatype",
                        "field", field,
                        "expectedType", expectedType
                )
        );
    }

    //  Validation errors
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
//
//        Map<String, String> errors = new HashMap<>();
//
//        ex.getBindingResult().getFieldErrors().forEach(error ->
//                errors.put(error.getField(), error.getDefaultMessage())
//        );
//
//        return ResponseEntity.badRequest().body(
//                Map.of(
//                        "status", 400,
//                        "message", "Validation failed",
//                        "errors", errors
//                )
//        );
//    }
}