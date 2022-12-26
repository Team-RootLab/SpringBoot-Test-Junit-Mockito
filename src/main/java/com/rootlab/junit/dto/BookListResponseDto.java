package com.rootlab.junit.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class BookListResponseDto {
	List<BookResponseDto> items;

	@Builder
	public BookListResponseDto(List<BookResponseDto> items) {
		this.items = items;
	}
}
