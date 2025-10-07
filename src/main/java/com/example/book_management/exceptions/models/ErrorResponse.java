package com.example.book_management.exceptions.models;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private List<FieldErrorDetail> fields;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(int status, String error, String message, List<FieldErrorDetail> fields) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.fields = fields;
    }
}