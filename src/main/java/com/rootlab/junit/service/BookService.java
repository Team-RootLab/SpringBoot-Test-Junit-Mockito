package com.rootlab.junit.service;

import com.rootlab.junit.domain.Book;
import com.rootlab.junit.dto.BookRequestDto;
import com.rootlab.junit.dto.BookResponseDto;
import com.rootlab.junit.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {
	private final BookRepository bookRepository;

	@Transactional(rollbackOn = RuntimeException.class)
	public BookResponseDto registerBook(BookRequestDto dto) {
		Book savedBook = bookRepository.save(dto.toEntity());
		return savedBook.toDto();
	}


}
