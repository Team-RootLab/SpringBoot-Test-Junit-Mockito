package com.rootlab.junit.service;

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
}
