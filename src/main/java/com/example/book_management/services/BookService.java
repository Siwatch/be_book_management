package com.example.book_management.services;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.book_management.exceptions.BusinessException;
import com.example.book_management.models.entity.Book;
import com.example.book_management.models.request.book.CreateBookRequest;
import com.example.book_management.models.response.ResponseData;
import com.example.book_management.models.response.book.BookResponse;
import com.example.book_management.repositories.book.BookRepository;
import com.example.book_management.util.helper.DateHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository _bookRepository;

    public ResponseData createBook(CreateBookRequest request) {

        LocalDate publishedDate;
        try {
            // แปลง string input เป็น LocalDate (แบบ CE Calent (eg. 2568-01-01 to 2025-01-01))
            publishedDate = DateHelper.parseBEToCE(request.getPublishedDate());
            // ตรวจสอบปี
            DateHelper.validateYear(publishedDate, 1000, LocalDate.now().getYear());

        } catch (DateTimeException | IllegalArgumentException e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        _bookRepository.save(Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publishedDate(publishedDate)
                .build());

        return new ResponseData<>(HttpStatus.OK, "Create Book Success!!");
    }

    public ResponseData<List<BookResponse>> getBooksByAuthorName(String author) {
        List<Book> listOfBook = author != null && !author.isBlank() ? _bookRepository.findByAuthorIgnoreCase(author.trim())
                : _bookRepository.findAll();
        return new ResponseData<List<BookResponse>>(HttpStatus.OK, listOfBook.stream().map(
                book -> BookResponse.builder()
                        .id(book.getId())
                        .title(book.getTitle())
                        .author(book.getAuthor())
                        .publishedDate(DateHelper.formatCEToBE(book.getPublishedDate()))
                        .build())
                .toList());
    }
}
