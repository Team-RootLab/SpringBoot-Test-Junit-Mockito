package com.rootlab.junit.repository;

import com.rootlab.junit.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // DB와 관련된 Component들만 메모리에 로드
class BookRepositoryTest {
	@Test
	public void registerBook() {
		// given
		String title = "title";
		String author = "author";
		// when
		Book book = Book.builder().title(title).author(author).build();
		// then
		assertEquals(title, book.getTitle());
		assertEquals(author, book.getAuthor());
	}
}