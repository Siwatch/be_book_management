package com.example.book_management.exceptions.models;

import java.util.List;

import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    private HttpStatusCode statusCode;
    private String error;
    private String message;
    private List<FieldErrorDetail> fields;

    public ErrorResponse(HttpStatusCode statusCode, String error, String message) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(HttpStatusCode statusCode, String error, String message, List<FieldErrorDetail> fields) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.fields = fields;
    }
}