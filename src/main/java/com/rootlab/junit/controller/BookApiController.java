package com.rootlab.junit.controller;

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
@RequestMapping("/api")
public class BookApiController {
	private final BookService bookService;

	@PostMapping("/v1/book")
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
}
