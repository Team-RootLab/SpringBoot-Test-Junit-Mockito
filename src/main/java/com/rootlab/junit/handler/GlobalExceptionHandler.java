package com.rootlab.junit.handler;

import com.rootlab.junit.dto.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> apiException(RuntimeException e) {
		CommonResponseDto<Object> commonResponseDto = CommonResponseDto.builder()
				.code(0)
				.msg(e.getMessage())
				.body("")
				.build();
		return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST); // 400 Bad Request
	}
}
