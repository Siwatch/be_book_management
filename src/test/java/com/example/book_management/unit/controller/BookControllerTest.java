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
        ResponseData serviceResponse = new ResponseData<>(HttpStatus.BAD_REQUEST, "Year must be between 1543 and " + java.time.LocalDate.now().getYear());
        when(bookService.createBook(request)).thenReturn(serviceResponse);

        ResponseEntity<ResponseData> response = bookController.createBook(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(serviceResponse.getMessage(), response.getBody().getMessage());
        verify(bookService).createBook(request);
    }

    @Test
    void getBooks_withAuthor() {
        String author = "John Doe";
        List<BookResponse> data = List.of(
                BookResponse.builder().id(1L).title("Java Basics").author("John Doe").publishedDate("2563-05-20").build()
        );
        ResponseData<List<BookResponse>> serviceResponse = new ResponseData<>(HttpStatus.OK, data);
        when(bookService.getBooksByAuthorName(author)).thenReturn(serviceResponse);

        ResponseEntity<ResponseData> response = bookController.getBooks(author);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        List<BookResponse> responseData = (List<BookResponse>) response.getBody().getData();
        assertEquals(1, responseData.size());
        assertEquals("John Doe", responseData.get(0).getAuthor());
        verify(bookService, times(1)).getBooksByAuthorName(author);
    }

    @Test
    void getBooks_withoutAuthor() {
        List<BookResponse> data = List.of(
                BookResponse.builder().id(1L).title("Java Basics").author("John Doe").publishedDate("2563-05-20").build(),
                BookResponse.builder().id(2L).title("Spring Boot API").author("Jane Smith").publishedDate("2564-03-10").build()
        );
        ResponseData<List<BookResponse>> serviceResponse = new ResponseData<>(HttpStatus.OK, data);
        when(bookService.getBooksByAuthorName(null)).thenReturn(serviceResponse);

        ResponseEntity<ResponseData> response = bookController.getBooks(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        List<BookResponse> responseData = (List<BookResponse>) response.getBody().getData();
        assertEquals(2, responseData.size());
        verify(bookService, times(1)).getBooksByAuthorName(null);
    }
}
