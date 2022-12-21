package com.rootlab.junit.repository;

import com.rootlab.junit.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // DB와 관련된 Component들만 메모리에 로드
class BookRepositoryTest {

	@Autowired
	BookRepository bookRepository;

	/**
	 * 가정 1 : [ 데이터준비 + 책등록 ] (T), [ 데이터준비 + 책목록보기 ] (T) -> 사이즈 1 (검증 완료)
	 * 가정 2 : [ 데이터준비 + 책등록 + 데이터준비 + 책목록보기 ] (T) -> 사이즈 2 (검증 실패)
	 */
	@BeforeEach
	@DisplayName("데이터준비하기")
	public void prepareBookData() {
		String title = "title";
		String author = "author";
		Book book = Book.builder()
				.title(title)
				.author(author)
				.build();
		bookRepository.save(book);
	}

	@Test
	@DisplayName("책등록하기")
	public void saveBookTest() {
		// given
		String title = "제목";
		String author = "저자";
		Book book = Book.builder()
				.title(title)
				.author(author)
				.build();
		// when
		Book savedBook = bookRepository.save(book);
		// then
		assertEquals(title, savedBook.getTitle());
		assertEquals(author, savedBook.getAuthor());
	}

	@Test
	@DisplayName("책목록보기")
	public void findAllBookTest() {
		// given
		long size = 1L;
		String title = "title";
		String author = "author";
		// when
		List<Book> books = bookRepository.findAll();
		// then
		assertEquals(size, books.size());
		assertEquals(title, books.get(0).getTitle());
		assertEquals(author, books.get(0).getAuthor());
	}

	@Test
	@DisplayName("책한권보기")
	public void findOneBookTest() {
		// given
		String title = "title";
		String author = "author";
		// when
//		Book savedBook = bookRepository.findAll().get(0);
		Book savedBook = bookRepository.findByTitle(title).get();
		System.out.println("savedBook = " + savedBook);
		// then
		assertEquals(title, savedBook.getTitle());
		assertEquals(author, savedBook.getAuthor());
	}
}