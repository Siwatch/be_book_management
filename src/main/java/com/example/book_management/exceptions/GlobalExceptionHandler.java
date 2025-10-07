package com.example.book_management.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.book_management.exceptions.models.ErrorResponse;
import com.example.book_management.exceptions.models.FieldErrorDetail;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getHttpStatus(),
                "BUSINESS_ERROR",
                ex.getMessage());
        log.error("BusinessException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(error, ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                ex.getMessage());
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // สำหรับ wrapper เวลา client ไม่ทำการ ส่ง field ที่ไม่ required หรือ invalid request property
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        // ทำกาเก็บ field ที่ทำให้เกิด method argument not valid exception
        List<FieldErrorDetail> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new FieldErrorDetail(err.getField(), err.getDefaultMessage()))
                .collect(Collectors.toList());

        String message = "Missing or invalid required fields";
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                message,
                fields);

        log.error("Validation error: {}", message, ex);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
