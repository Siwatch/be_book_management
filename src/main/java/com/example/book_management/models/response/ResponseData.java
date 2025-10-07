package com.example.book_management.models.response;

import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseData<T> {
    private HttpStatusCode statusCode;
    private String message;
    private T data;

    public ResponseData(HttpStatusCode statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public ResponseData(HttpStatusCode statusCode, T data) {
        this.statusCode = statusCode;
        this.data = data;
    } 

    public ResponseData(HttpStatusCode statusCode,String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    } 
}
