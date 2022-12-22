package com.rootlab.junit.dto;

import com.rootlab.junit.domain.Book;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequestDto {
	private String title;
	private String author;

	public Book toEntity() {
		return Book.builder()
				.title(title)
				.author(author)
				.build();
	}
}
