package com.example.book_management.repositories.book;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.book_management.models.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthorStartingWithIgnoreCase(String author);
    Page<Book> findByAuthorStartingWithIgnoreCase(String author, Pageable pageable);
}
