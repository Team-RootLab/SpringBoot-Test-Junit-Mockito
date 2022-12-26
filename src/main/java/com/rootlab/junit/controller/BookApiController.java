package com.rootlab.junit.controller;

import com.rootlab.junit.dto.BookRequestDto;
import com.rootlab.junit.dto.BookResponseDto;
import com.rootlab.junit.dto.CommonResponseDto;
import com.rootlab.junit.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookApiController {
	private final BookService bookService;

	@PostMapping("/v1/book")
	public ResponseEntity<?> saveBook(@RequestBody BookRequestDto dto) {
		BookResponseDto responseDto = bookService.registerBook(dto);
		CommonResponseDto<Object> commonResponseDto = CommonResponseDto.builder()
				.code(1)
				.msg("/api/v1/book POST 요청 성공")
				.body(responseDto)
				.build();
		return new ResponseEntity<>(commonResponseDto, HttpStatus.CREATED); // 201 created
	}
}
