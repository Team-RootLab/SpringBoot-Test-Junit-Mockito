package com.rootlab.junit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.rootlab.junit.dto.BookRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // (C, S, R) 통합테스트
class BookApiControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private static ObjectMapper mapper;
	private static HttpHeaders headers;

	@BeforeAll
	public static void init() {
		mapper = new ObjectMapper();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	@Test
	public void saveBookTest() throws JsonProcessingException {
	    // given
		BookRequestDto requestDto = new BookRequestDto();
		requestDto.setTitle("title");
		requestDto.setAuthor("author");
		String body = mapper.writeValueAsString(requestDto);
		// when
		HttpEntity<String> request = new HttpEntity<>(body, headers);
		ResponseEntity<String> response = restTemplate.exchange("/api/v1/book", HttpMethod.POST, request, String.class);
//		ResponseEntity<? extends Object> response = restTemplate.exchange("/api/v1/book", HttpMethod.POST, request, String.class);
//		System.out.println("body = " + response.getBody());
	    // then
		DocumentContext context = JsonPath.parse(response.getBody());
		String title = context.read("$.body.title");
		String author = context.read("$.body.author");
		assertThat(title).isEqualTo(requestDto.getTitle());
		assertThat(author).isEqualTo(requestDto.getAuthor());
	}
}