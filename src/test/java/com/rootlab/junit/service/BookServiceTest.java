package com.rootlab.junit.service;

import com.rootlab.junit.dto.BookRequestDto;
import com.rootlab.junit.dto.BookResponseDto;
import com.rootlab.junit.repository.BookRepository;
import com.rootlab.junit.util.MailSender;
import com.rootlab.junit.util.MailSenderAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookServiceTest {

	@Autowired
	BookRepository bookRepository;

	@Test
	@DisplayName("책등록하기")
	public void registerBookTest() {
	    // given
		String title = "title";
		String author = "author";
		BookRequestDto requestDto = new BookRequestDto();
		requestDto.setTitle(title);
		requestDto.setAuthor(author);
		// stub
//		MailSender mailSender = new MailSenderStub();
		MailSender mailSender = new MailSenderAdapter();
	    // when
		BookService bookService = new BookService(bookRepository, mailSender);
		BookResponseDto responseDto = bookService.registerBook(requestDto);
		// then
		assertEquals(responseDto.getTitle(), requestDto.getTitle());
		assertEquals(responseDto.getAuthor(), requestDto.getAuthor());
	}

}