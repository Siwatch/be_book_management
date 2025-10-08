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
        void createBook_Success() throws Exception {
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

                Book created = bookRepository.findByAuthorIgnoreCase("John Doe").stream()
                                .filter(b -> "Java Basics 2".equals(b.getTitle()))
                                .findFirst()
                                .orElseThrow();

                assertEquals(LocalDate.of(2025, 1, 1), created.getPublishedDate());
        }

        @Test
        void createBook_fail_whenMissingRequireFieldTitle() throws Exception {
                String requestBody = """
                                        {
                                           "author" : "john doe"
                                        }
                                        """;
                mockMvc.perform(MockMvcRequestBuilders.post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Missing or invalid required fields"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.fields[0].field").value("title"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.fields[0].message").value("title is required"));
                
        }

        @Test
        void createBook_fail_whenMissingRequireFieldAuthor() throws Exception {
                String requestBody = """
                                        {
                                           "title" : "Spiderman and friends"
                                        }
                                        """;
                mockMvc.perform(MockMvcRequestBuilders.post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Missing or invalid required fields"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.fields[0].field").value("author"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.fields[0].message").value("author is required"));
                
        }

        @Test
        void createBook_fail_whenInvalidPublishedDateFormat() throws Exception {
                String requestBody = """
                                        {
                                            "title": "Java Basics 2",
                                            "author": "John Doe",
                                            "publishedDate": "01-01-2568"
                                        }
                                        """;
                mockMvc.perform(MockMvcRequestBuilders.post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid date format. Expected yyyy-MM-dd"));
        }

        @Test
        void createBook_fail_whenInvalidPublishedYearCE() throws Exception {
                String requestBody = """
                                {
                                  "title": "Java Basics 2",
                                  "author": "John Doe",
                                  "publishedDate": "2000-01-01"
                                }
                                """;
                mockMvc.perform(MockMvcRequestBuilders.post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Expected Buddhist year (>= 2400), got: 2000"));
        }

        @Test
        void createBook_fail_whenInvalidPublishedDateOrMonth() throws Exception {
                // กรณี leap year ปีที่เดือนกุมภามี 29 วัน (ไม่ใช่ปี 2568)
                String requestBody = """
                                {
                                  "title": "Java Basics 2",
                                  "author": "John Doe",
                                  "publishedDate": "2568-02-29" 
                                }
                                """;
                mockMvc.perform(MockMvcRequestBuilders.post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid date (day/month) for given year"));
        }

        @Test
        void createBook_fail_whenValidatePublishedYear() throws Exception {
                // กรณีปีมากกว่าปัจจุบัน
                String requestBody = """
                                {
                                  "title": "Java Basics 2",
                                  "author": "John Doe",
                                  "publishedDate": "2569-01-01" 
                                }
                                """;
                int currentYear = LocalDate.now().getYear();
                mockMvc.perform(MockMvcRequestBuilders.post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Published year (BE 2569 / CE 2026) must be between 1000 and " + currentYear));
        }

        @Test
        void getBooks_withAuthor() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.get("/books")
                                .param("author", "John Doe")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].author").value("John Doe"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title").value("Java Basics"));
        }

        @Test
        void getBooks_withoutAuthor() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.get("/books")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(2));
        }

        @Test
        void getBooks_withoutAuthor_whenAuthorIsEmpty() throws Exception {
                // กรณีไม่ส่งหรือส่งมาเป็น Blank, Whitspace ให้ search all 
                mockMvc.perform(MockMvcRequestBuilders.get("/books")
                                .param("author"," ")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(2));
        }
}
