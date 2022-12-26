package com.rootlab.junit.controller;

import com.rootlab.junit.dto.BookListResponseDto;
import com.rootlab.junit.dto.BookRequestDto;
import com.rootlab.junit.dto.BookResponseDto;
import com.rootlab.junit.dto.CommonResponseDto;
import com.rootlab.junit.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BookApiController {
	private final BookService bookService;

	@PostMapping("/book")
	public ResponseEntity<?> saveBook(@RequestBody @Valid BookRequestDto dto, BindingResult bindingResult) {

		// AOP 방식으로도 처리가능
		if (bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError fe : bindingResult.getFieldErrors()) {
				errorMap.put(fe.getField(), fe.getDefaultMessage());
			}
			throw new RuntimeException(errorMap.toString());
		}

		BookResponseDto responseDto = bookService.registerBook(dto);
		CommonResponseDto<Object> commonResponseDto = CommonResponseDto.builder()
				.code(1)
				.msg("/api/v1/book POST 요청 성공")
				.body(responseDto)
				.build();
		return new ResponseEntity<>(commonResponseDto, HttpStatus.CREATED); // 201 created
	}

	@GetMapping("/book")
	public ResponseEntity<?> getBookList() {
		BookListResponseDto bookList = bookService.getBookDtoList();
		CommonResponseDto<Object> commonResponseDto = CommonResponseDto.builder()
				.code(1)
				.msg("/api/v1/book GET 요청 성공")
				.body(bookList)
				.build();
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK); // 200 ok
	}

	@GetMapping("/book/{id}")
	public ResponseEntity<?> getBookItem(@PathVariable Long id) {
		BookResponseDto dto = bookService.getBookDto(id);
		CommonResponseDto<Object> commonResponseDto = CommonResponseDto.builder()
				.code(1)
				.msg("/api/v1/book/" + id + " GET 요청 성공")
				.body(dto)
				.build();
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK); // 200 ok
	}

	@DeleteMapping("/book/{id}")
	public ResponseEntity<?> deleteBook(@PathVariable Long id) {
		bookService.deleteBook(id);
		CommonResponseDto<Object> commonResponseDto = CommonResponseDto.builder()
				.code(1)
				.msg("/api/v1/book/" + id + " DELETE 요청 성공")
				.body("")
				.build();
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK); // 200 ok
	}

	@PutMapping("/book/{id}")
	public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody @Valid BookRequestDto dto,
										BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError fe : bindingResult.getFieldErrors()) {
				errorMap.put(fe.getField(), fe.getDefaultMessage());
			}
			throw new RuntimeException(errorMap.toString());
		}

		BookResponseDto bookResponseDto = bookService.updateBook(id, dto);
		CommonResponseDto<Object> commonResponseDto = CommonResponseDto.builder()
				.code(1)
				.msg("/api/v1/book/" + id + " PUT 요청 성공")
				.body(bookResponseDto)
				.build();
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK); // 200 ok
	}

}
