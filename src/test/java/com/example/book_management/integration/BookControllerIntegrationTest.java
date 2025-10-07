package com.example.book_management.integration;

import com.example.book_management.models.entity.Book;
import com.example.book_management.repositories.book.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();
        bookRepository.saveAll(List.of(
                new Book(null, "Java Basics", "John Doe", LocalDate.of(2020, 5, 20)),
                new Book(null, "Spring Boot API", "Jane Smith", LocalDate.of(2021, 3, 10))));
    }

    @Test
    void testCreateBook() throws Exception {
        String requestBody = """
                {
                  "title": "Java Basics 2",
                  "author": "John Doe",
                  "publishedDate": "2568-01-01"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Create Book Success!!"));

        Book created = bookRepository.findByAuthor("John Doe").stream()
                .filter(b -> "Java Basics 2".equals(b.getTitle()))
                .findFirst()
                .orElseThrow();

        assertEquals(LocalDate.of(2025, 1, 1), created.getPublishedDate());
    }

    @Test
    void testGetBooksByAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/books")
                .param("author", "John Doe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].author").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title").value("Java Basics"));
    }

    @Test
    void testGetBooksWithoutAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(2));
    }
}
