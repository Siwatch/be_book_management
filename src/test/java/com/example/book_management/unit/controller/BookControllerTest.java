package com.example.book_management.unit.controller;

import com.example.book_management.controllers.BookController;
import com.example.book_management.models.request.book.CreateBookRequest;
import com.example.book_management.models.response.ResponseData;
import com.example.book_management.models.response.book.BookResponse;
import com.example.book_management.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @Test
    void createBook_whenSuccess() {
        CreateBookRequest request = new CreateBookRequest("Java Basics", "John Doe", "2568-01-01");
        ResponseData serviceResponse = new ResponseData<>(HttpStatus.OK, "Create Book Success!!");
        when(bookService.createBook(request)).thenReturn(serviceResponse);

        ResponseEntity<ResponseData> response = bookController.createBook(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Create Book Success!!", response.getBody().getMessage());
        verify(bookService).createBook(request);
    }

    @Test
    void createBook_whenError() {
        CreateBookRequest request = new CreateBookRequest("Title", "Author", "1000-01-01");
        ResponseData serviceResponse = new ResponseData<>(HttpStatus.BAD_REQUEST, "Year must be between 1000 and " + java.time.LocalDate.now().getYear());
        when(bookService.createBook(request)).thenReturn(serviceResponse);

        ResponseEntity<ResponseData> response = bookController.createBook(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(bookService).createBook(request);
    }

    @Test
    void getBooks_withAuthor() {
        String author = "John Doe";
        List<BookResponse> content = List.of(
                BookResponse.builder().id(1L).title("Java Basics").author("John Doe").publishedDate("2563-05-20").build()
        );
        Pageable pageable = PageRequest.of(0, 20);
        Page<BookResponse> page = new PageImpl<>(content, pageable, content.size());
        ResponseData<Page<BookResponse>> serviceResponse = new ResponseData<>(HttpStatus.OK, page);
        when(bookService.getBooksByAuthorName(author, pageable)).thenReturn(serviceResponse);

        ResponseEntity<ResponseData> response = bookController.getBooks(author, pageable, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Page<BookResponse> responsePage = (Page<BookResponse>) response.getBody().getData();
        assertEquals(1, responsePage.getTotalElements());
        assertEquals("John Doe", responsePage.getContent().get(0).getAuthor());
        verify(bookService, times(1)).getBooksByAuthorName(author, pageable);
    }

    @Test
    void getBooks_withoutAuthor() {
        List<BookResponse> content = List.of(
                BookResponse.builder().id(1L).title("Java Basics").author("John Doe").publishedDate("2563-05-20").build(),
                BookResponse.builder().id(2L).title("Spring Boot API").author("Jane Smith").publishedDate("2564-03-10").build()
        );
        Pageable pageable = PageRequest.of(0, 20);
        Page<BookResponse> page = new PageImpl<>(content, pageable, content.size());
        ResponseData<Page<BookResponse>> serviceResponse = new ResponseData<>(HttpStatus.OK, page);
        when(bookService.getBooksByAuthorName(null, pageable)).thenReturn(serviceResponse);

        ResponseEntity<ResponseData> response = bookController.getBooks(null, pageable, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Page<BookResponse> responsePage = (Page<BookResponse>) response.getBody().getData();
        assertEquals(2, responsePage.getTotalElements());
        verify(bookService, times(1)).getBooksByAuthorName(null, pageable);
    }
}
