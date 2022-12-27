package com.rootlab.junit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.rootlab.junit.domain.Book;
import com.rootlab.junit.dto.BookRequestDto;
import com.rootlab.junit.dto.BookResponseDto;
import com.rootlab.junit.repository.BookRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.EnumSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/** @ActiveProfiles("dev")를 붙인 이유:
 * local 환경은 window / 테스트 환경, 서비스 환경은 linux라고 가정
 * local 환경과 서비스 환경이 다르기 때문에 서비스 환경과 유사한 테스트 환경에서 테스트 해야 함
 * 테스트 서버에서 dev profile로 전체 테스트 후 빌드된 jar 파일을 서비스 서버로 옮겨 배포함
 */

// TODO: Controller Layer Unit Test 작성해보기
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // (C, S, R) 통합테스트
class BookApiControllerTest {

	@Autowired
	private BookRepository bookRepository;

	/**
	 * TestRestTemplate은 REST 방식으로 개발한 API의 Test를 최적화 하기 위해 만들어진 클래스이며
	 * HTTP 요청 후 데이터를 응답 받을 수 있는 템플릿 객체이다.
	 * getForObject() - 기본 http 헤더를 사용하여 결과를 객체로 반환받는다.
	 * getForEntity() - 기본 http 헤더를 사용하여 결과를 ResponseEntity로 반환받는다.
	 * exchange() - 결과를 ResponseEntity로 반환받는다. Http header를 변경할 수 있다.
	 */

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

	@BeforeEach
	@DisplayName("데이터준비하기")
	public void prepareBookData() {
		String title = "title";
		String author = "author";
		Book book = Book.builder()
				.title(title)
				.author(author)
				.build();
		bookRepository.save(book);
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

	@Test
	@Sql("classpath:sql/initTable.sql")
	public void getBookList() {
		// when
		HttpEntity<String> request = new HttpEntity<>("", headers);
		ResponseEntity<String> response = restTemplate.exchange("/api/v1/book", HttpMethod.GET, request, String.class);
		// then

		/**
		 * context.read("$.body.items[0]")의 결과를 POJO로 변환하기 위해
		 * JacksonMappingProvider와 JacksonJsonProvider를 사용하도록 설정함
		 * 공식문서 https://github.com/json-path/JsonPath "What is Returned When?"문단 참고
		 */
		Configuration.setDefaults(new Configuration.Defaults() {
			private final JsonProvider jsonProvider = new JacksonJsonProvider();
			private final MappingProvider mappingProvider = new JacksonMappingProvider();

			@Override
			public JsonProvider jsonProvider() {
				return jsonProvider;
			}

			@Override
			public MappingProvider mappingProvider() {
				return mappingProvider;
			}

			@Override
			public Set<Option> options() {
				return EnumSet.noneOf(Option.class);
			}
		});

		DocumentContext context = JsonPath.parse(response.getBody());
		Integer code = context.read("$.code");
		BookResponseDto bookResponseDto = context.read("$.body.items[0]", BookResponseDto.class);
		assertThat(code).isEqualTo(1);
		assertThat(bookResponseDto.getTitle()).isEqualTo("title");
		assertThat(bookResponseDto.getAuthor()).isEqualTo("author");
	}

	@Test
	@Sql("classpath:sql/initTable.sql")
	public void getBookItem() {
		// given
		Integer id = 1;
		// when
		HttpEntity<String> request = new HttpEntity<>("", headers);
		ResponseEntity<String> response = restTemplate.exchange("/api/v1/book/" + id, HttpMethod.GET, request, String.class);
		// then
		DocumentContext context = JsonPath.parse(response.getBody());
		Integer code = context.read("$.code");
		String title = context.read("$.body.title");
		String author = context.read("$.body.author");
		assertThat(code).isEqualTo(1);
		assertThat(title).isEqualTo("title");
		assertThat(author).isEqualTo("author");
	}

	@Test
	@Sql("classpath:sql/initTable.sql")
	public void deleteBook() {
		// given
		Integer id = 1;
		// when
		HttpEntity<String> request = new HttpEntity<>("", headers);
		ResponseEntity<String> response = restTemplate.exchange("/api/v1/book/" + id, HttpMethod.DELETE, request, String.class);
		// then
		DocumentContext context = JsonPath.parse(response.getBody());
		Integer code = context.read("$.code");
		assertThat(code).isEqualTo(1);
	}

	@Test
	@Sql("classpath:sql/initTable.sql")
	public void updateBook() throws JsonProcessingException {
		// given
		Integer id = 1;
		BookRequestDto requestDto = new BookRequestDto();
		requestDto.setTitle("updated title");
		requestDto.setAuthor("updated author");
		String body = mapper.writeValueAsString(requestDto);
		// when
		HttpEntity<String> request = new HttpEntity<>(body, headers);
		ResponseEntity<String> response = restTemplate.exchange("/api/v1/book/" + id, HttpMethod.PUT, request, String.class);
		// then
		DocumentContext context = JsonPath.parse(response.getBody());
//		System.out.println("body = " + response.getBody());
		String title = context.read("$.body.title");
		String author = context.read("$.body.author");
		assertThat(title).isEqualTo(requestDto.getTitle());
		assertThat(author).isEqualTo(requestDto.getAuthor());
	}
}