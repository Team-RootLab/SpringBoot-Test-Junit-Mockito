package com.rootlab.junit.service;

import com.rootlab.junit.domain.Book;
import com.rootlab.junit.dto.BookRequestDto;
import com.rootlab.junit.dto.BookResponseDto;
import com.rootlab.junit.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
	private final BookRepository bookRepository;

	@Transactional(rollbackOn = RuntimeException.class)
	public BookResponseDto registerBook(BookRequestDto dto) {
		Book savedBook = bookRepository.save(dto.toEntity());
		return savedBook.toDto();
	}

	public List<BookResponseDto> getBookDtoList() {
		return bookRepository.findAll().stream()
//				.map((book) -> book.toDto())
				.map(Book::toDto)
				.collect(Collectors.toList());
	}

	public BookResponseDto getBookDto(Long id) {
		Optional<Book> book = bookRepository.findById(id);
		book.orElseThrow(
				() -> new RuntimeException("해당 id의 Book 데이터를 찾을 수 없습니다.")
		);
		return book.get().toDto();
	}

}
