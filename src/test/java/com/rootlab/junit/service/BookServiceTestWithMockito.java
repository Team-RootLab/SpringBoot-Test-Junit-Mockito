package com.rootlab.junit.service;

import com.rootlab.junit.domain.Book;
import com.rootlab.junit.dto.BookRequestDto;
import com.rootlab.junit.dto.BookResponseDto;
import com.rootlab.junit.repository.BookRepository;
import com.rootlab.junit.util.MailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTestWithMockito {

	@Mock
	BookRepository bookRepository;

	@Mock
	MailSender mailSender;

	@InjectMocks
	BookService bookService;

	@Test
	@DisplayName("책등록하기")
	public void registerBookTest() {
		// given
		String title = "title";
		String author = "author";
		BookRequestDto requestDto = new BookRequestDto();
		requestDto.setTitle(title);
		requestDto.setAuthor(author);
		// stub: 가설, 행동정의
//		when(bookRepository.save(requestDto.toEntity())).thenReturn(requestDto.toEntity());
		when(bookRepository.save(any())).thenReturn(requestDto.toEntity());
		when(mailSender.send()).thenReturn(true);
		// when
		BookResponseDto responseDto = bookService.registerBook(requestDto);
		// then: AssertJ 사용하기
		assertThat(responseDto.getTitle()).isEqualTo(responseDto.getTitle());
		assertThat(responseDto.getAuthor()).isEqualTo(responseDto.getAuthor());
	}

	@Test
	@DisplayName("책목록보기")
	public void getBookDtoListTest() {
		// given
		List<Book> books = Arrays.asList(
				new Book(1L, "title1", "author1"),
				new Book(2L, "title2", "author2")
		);
		// stub
		when(bookRepository.findAll()).thenReturn(books);
		// when
		List<BookResponseDto> bookDtoList = bookService.getBookDtoList();
		// then
		assertThat(bookDtoList.get(0).getTitle()).isEqualTo(books.get(0).getTitle());
		assertThat(bookDtoList.get(0).getAuthor()).isEqualTo(books.get(0).getAuthor());
		assertThat(bookDtoList.get(1).getTitle()).isEqualTo(books.get(1).getTitle());
		assertThat(bookDtoList.get(1).getAuthor()).isEqualTo(books.get(1).getAuthor());
	}

	@Test
	@DisplayName("책한권보기")
	public void getBookDtoTest() {
		// given
		Long id = 1L;
		Book book = Book.builder()
				.id(id)
				.title("title")
				.author("author")
				.build();
		Optional<Book> optionalBook = Optional.of(book);
		// stub
		when(bookRepository.findById(id)).thenReturn(optionalBook);
		// when
		BookResponseDto dto = bookService.getBookDto(id);
		// then
		assertThat(dto.getTitle()).isEqualTo(book.getTitle());
		assertThat(dto.getAuthor()).isEqualTo(book.getAuthor());
	}

	@Test
	@DisplayName("책수정하기")
	public void updateBookTest() {
		// given
		Long id = 1L;
		BookRequestDto requestDto = new BookRequestDto();
		requestDto.setTitle("title2");
		requestDto.setAuthor("author2");
		// stub
		Book book = new Book(1L, "title1", "author1");
		Optional<Book> optionalBook = Optional.of(book);
		when(bookRepository.findById(id)).thenReturn(optionalBook);
		// when
		BookResponseDto responseDto = bookService.updateBook(1L, requestDto);
		// then
		assertThat(responseDto.getTitle()).isEqualTo(requestDto.getTitle());
		assertThat(responseDto.getAuthor()).isEqualTo(requestDto.getAuthor());
	}
}
