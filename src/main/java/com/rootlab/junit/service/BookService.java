package com.rootlab.junit.service;

import com.rootlab.junit.domain.Book;
import com.rootlab.junit.dto.BookListResponseDto;
import com.rootlab.junit.dto.BookRequestDto;
import com.rootlab.junit.dto.BookResponseDto;
import com.rootlab.junit.repository.BookRepository;
import com.rootlab.junit.util.MailSender;
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
	private final MailSender mailSender;


	@Transactional(rollbackOn = RuntimeException.class)
	public BookResponseDto registerBook(BookRequestDto dto) {
		Book savedBook = bookRepository.save(dto.toEntity());
		Optional<Book> optionalBook = Optional.of(savedBook);
		optionalBook.ifPresent(
				(book) -> {
					if (!mailSender.send()) {
						throw new RuntimeException("메일이 전송되지 않았습니다.");
					}
				}
		);
		return savedBook.toDto();
	}

	public BookListResponseDto getBookDtoList() {
		List<BookResponseDto> dtos = bookRepository.findAll().stream()
//				.map((book) -> book.toDto())
				.map(Book::toDto)
				.collect(Collectors.toList());
		return BookListResponseDto.builder().items(dtos).build();
	}

	public BookResponseDto getBookDto(Long id) {
		Book book = bookRepository.findById(id).orElseThrow(() -> {
			throw new RuntimeException("해당 id의 Book 데이터를 찾을 수 없습니다.");
		});

		return book.toDto();
	}

	@Transactional(rollbackOn = RuntimeException.class)
	public void deleteBook(Long id) {
		bookRepository.deleteById(id);
	}

	// https://middleearth.tistory.com/11
	// @Setter없이 Entity를 update하자
	@Transactional(rollbackOn = RuntimeException.class)
	public BookResponseDto updateBook(Long id, BookRequestDto dto) {
		Book book = bookRepository.findById(id).orElseThrow(() -> {
			throw new RuntimeException("해당 id의 Book 데이터를 찾을 수 없습니다.");
		});
		book.update(dto.getTitle(), dto.getAuthor());
		return book.toDto();
	}

}
