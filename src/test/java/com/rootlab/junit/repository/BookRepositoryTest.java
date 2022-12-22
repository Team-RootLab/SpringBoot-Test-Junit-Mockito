package com.rootlab.junit.repository;

import com.rootlab.junit.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

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
	@DisplayName("책한권보기1")
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

	@Test
	@Sql("classpath:sql/initTable.sql")
	@DisplayName("책한권보기2")
	public void findOneBookTestWithSql() {
		// given
		String title = "title";
		String author = "author";
		// when
		Book savedBook = bookRepository.findById(1L).get();
		System.out.println("savedBook = " + savedBook);
		// then
		assertEquals(title, savedBook.getTitle());
		assertEquals(author, savedBook.getAuthor());
	}

	@Test
	@DisplayName("책삭제하기1")
	public void deleteBookTest() {
		// given
		String title = "title";
		Book savedBook = bookRepository.findByTitle(title).get();
		Long id = savedBook.getId();
		// when
		bookRepository.deleteById(id);
		// then
		Optional<Book> optionalBook = bookRepository.findById(id);
		assertFalse(optionalBook.isPresent());
	}

	@Test
	@Sql("classpath:sql/initTable.sql")
	@DisplayName("책삭제하기2")
	public void deleteBookTestWithSql() {
		// given
		Long id = 1L;
		// when
		bookRepository.deleteById(id);
		// then
		Optional<Book> optionalBook = bookRepository.findById(id);
		assertFalse(optionalBook.isPresent());
	}

	@Test
	@Sql("classpath:sql/initTable.sql")
	@DisplayName("책정보수정하기1")
	public void updateBookTestWithSql() {
		// given
		Long id = 1L;
		String title = "updatedTitle";
		String author = "updatedAuthor";
		// when
		// @Sql로 Table을 초기화해도 메모리상에 데이터가 존재함 확인
//		bookRepository.findAll().stream().forEach(
//				(book) -> {
//					System.out.println("book = " + book);
//				}
//		);
		Book newBook = new Book(id, title, author);
		Book savedBook = bookRepository.save(newBook);
		// then
//		bookRepository.findAll().stream().forEach(
//				(book) -> {
//					System.out.println("book = " + book);
//				}
//		);
		assertEquals(title, savedBook.getTitle());
		assertEquals(author, savedBook.getAuthor());
	}

	@Test
	@DisplayName("책정보수정하기2")
	public void updateBookTest() {
		// given
		String title = "title";
		String updatedTitle = "updatedTitle";
		String updatedAuthor = "updatedAuthor";
		// when
		Book savedBook = bookRepository.findByTitle(title).get();
		savedBook.setTitle(updatedTitle);
		savedBook.setAuthor(updatedAuthor);
		// then
		assertEquals(updatedTitle, savedBook.getTitle());
		assertEquals(updatedAuthor, savedBook.getAuthor());
	}
}