package com.example.book_management.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.PageRequest;

import com.example.book_management.models.request.book.CreateBookRequest;
import com.example.book_management.models.response.ResponseData;
import com.example.book_management.services.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
public class BookController {
    private final BookService _bookService;

    @PostMapping
    public ResponseEntity<ResponseData> createBook(@Valid @RequestBody CreateBookRequest request) {
        ResponseData res = _bookService.createBook(request);
        return new ResponseEntity<>(res, res.getStatusCode());
    }

    @GetMapping
    public ResponseEntity<ResponseData> getBooks(
            @RequestParam(required = false) String author,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false, defaultValue = "20") Integer size) {

        int cappedSize = Math.min(size != null ? size : 20, 100);
        Pageable sanitized = PageRequest.of(pageable.getPageNumber(), cappedSize, pageable.getSort());

        ResponseData res = _bookService.getBooksByAuthorName(author, sanitized);
        return new ResponseEntity<>(res, res.getStatusCode());
    }
}
