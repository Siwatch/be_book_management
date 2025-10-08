package com.example.book_management.unit.service;

import com.example.book_management.exceptions.BusinessException;
import com.example.book_management.models.entity.Book;
import com.example.book_management.models.request.book.CreateBookRequest;
import com.example.book_management.models.response.ResponseData;
import com.example.book_management.models.response.book.BookResponse;
import com.example.book_management.repositories.book.BookRepository;
import com.example.book_management.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void createBook_Success() {
        CreateBookRequest request = new CreateBookRequest("Java Basics", "John Doe", "2568-01-01");

        ResponseData response = bookService.createBook(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Create Book Success!!", response.getMessage());

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        Book saved = captor.getValue();
        assertEquals("Java Basics", saved.getTitle());
        assertEquals("John Doe", saved.getAuthor());
        assertEquals(LocalDate.of(2025, 1, 1), saved.getPublishedDate());
    }

    @Test
    void createBook_whenInvalidDateFormat() {
        CreateBookRequest request = new CreateBookRequest("T", "A", "invalid-date");

        BusinessException ex = assertThrows(BusinessException.class, () -> bookService.createBook(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
    }

    @Test
    void createBook_whenYearOutOfRange() {
        CreateBookRequest request = new CreateBookRequest("T", "A", "1200-01-01");

        BusinessException ex = assertThrows(BusinessException.class, () -> bookService.createBook(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
    }

    @Test
    void getBooks_withAuthor() {
        String author = "John Doe";
        List<Book> books = List.of(
                Book.builder().id(1L).title("Java Basics").author(author).publishedDate(LocalDate.of(2020, 5, 20)).build()
        );
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findByAuthorStartingWithIgnoreCase(author, pageable))
                .thenReturn(new PageImpl<>(books, pageable, books.size()));

        ResponseData<Page<BookResponse>> response = bookService.getBooksByAuthorName(author, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<BookResponse> page = response.getData();
        assertEquals(1, page.getTotalElements());
        assertEquals("Java Basics", page.getContent().get(0).getTitle());
        assertEquals("John Doe", page.getContent().get(0).getAuthor());
        assertEquals("2563-05-20", page.getContent().get(0).getPublishedDate());
        verify(bookRepository, times(1)).findByAuthorStartingWithIgnoreCase(author, pageable);
    }

    @Test
    void getBooks_withoutAuthor() {
        List<Book> books = List.of(
                Book.builder().id(1L).title("Java Basics").author("John Doe").publishedDate(LocalDate.of(2020, 5, 20)).build(),
                Book.builder().id(2L).title("Spring Boot API").author("Jane Smith").publishedDate(LocalDate.of(2021, 3, 10)).build()
        );
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(pageable)).thenReturn(new PageImpl<>(books, pageable, books.size()));

        ResponseData<Page<BookResponse>> response = bookService.getBooksByAuthorName(null, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<BookResponse> page = response.getData();
        assertEquals(2, page.getTotalElements());
        assertEquals("2563-05-20", page.getContent().get(0).getPublishedDate());
        assertEquals("2564-03-10", page.getContent().get(1).getPublishedDate());
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void getBooks_withBlankAuthor() {
        List<Book> books = List.of(
                Book.builder().id(1L).title("Java Basics").author("John Doe").publishedDate(LocalDate.of(2020, 5, 20)).build()
        );
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(pageable)).thenReturn(new PageImpl<>(books, pageable, books.size()));

        ResponseData<Page<BookResponse>> response = bookService.getBooksByAuthorName("   ", pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<BookResponse> page = response.getData();
        assertEquals(1, page.getTotalElements());
        assertEquals("Java Basics", page.getContent().get(0).getTitle());
        verify(bookRepository, times(1)).findAll(pageable);
    }
}
