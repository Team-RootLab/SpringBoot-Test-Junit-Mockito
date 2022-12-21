package com.rootlab.junit.repository;

import com.rootlab.junit.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
