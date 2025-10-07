package com.example.book_management.models.response;

import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseData<T> {
    private HttpStatusCode status;
    private String message;
    private T data;

    public ResponseData(HttpStatusCode status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseData(HttpStatusCode status, T data) {
        this.status = status;
        this.data = data;
    } 

    public ResponseData(HttpStatusCode status,String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    } 
}
