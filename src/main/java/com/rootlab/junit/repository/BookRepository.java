package com.rootlab.junit.repository;

import com.rootlab.junit.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
	Optional<Book> findByTitle(String title);
}
