package com.rootlab.junit.dto;

import com.rootlab.junit.domain.Book;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class BookRequestDto {
	@NotBlank // NotNull + NotEmpty
	@Size(min = 1, max = 50)
	private String title;

	@NotBlank // NotNull + NotEmpty
	@Size(min = 2, max = 20)
	private String author;

	public Book toEntity() {
		return Book.builder()
				.title(title)
				.author(author)
				.build();
	}
}
