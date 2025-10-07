package com.example.book_management.exceptions.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldErrorDetail {
    private String field;
    private String message;

    public FieldErrorDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }
}