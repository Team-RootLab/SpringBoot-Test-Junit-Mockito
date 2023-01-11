package com.rootlab.junit.test;

import com.rootlab.junit.test.entity.Order;
import com.rootlab.junit.test.entity.Payment;
import com.rootlab.junit.test.repository.OrderRepository;
import com.rootlab.junit.test.repository.PaymentRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Spring의 테스트 프레임워크는 테스트 간에 애플리케이션 컨텍스트를 캐시한다.
 * 이는 후속 테스트가 동일한 구성을 사용하는 경우 이미 로드된 애플리케이션 컨텍스트를 사용할 수 있기 때문에 더 빠르게 시작됨을 의미한다.
 * 다른 테스트에 다른 구성이 필요한 경우 Spring Boot는 애플리케이션 컨텍스트를 캐시할 수 없으며 해당 구성으로 새 컨텍스트를 로드한다.
 * 따라서 @MockBean, @ActiveProfiles, @DynamicPropertySource 또는 구성을 사용자 정의하는 다른 annotation을 사용할 때마다 Spring은 테스트를 위한 새로운 애플리케이션 컨텍스트를 생성할 것이다.
 * Spring Boot 통합 테스트의 일반적인 실수는 @SpringBootTest로 모든 테스트를 시작한 다음 특정 사례에 대해 각 테스트를 구성하려고 시도하는 것이다.
 * 이 접근 방식은 일반적으로 Spring Boot가 테스트에 사용된 애플리케이션 컨텍스트를 캐시할 수 없기 때문에 매우 느리다.
 * 또한 필요한 것보다 훨씬 더 복잡한 테스트 구성으로 실행하게 된다.
 * 더 나은 접근 방식은 @WebMvcTest 및 @DataJpaTest와 같은 테스트 슬라이스를 최대한 유지하는 것이다.
 * 더 광범위한 통합 테스트의 경우 @SpringBootTest를 사용하여 모든 테스트에 대한 단일 구성을 작성하는 것이 좋다.
 */

/**
 * 테스트에서 임베디드 웹 서버를 시작할 때 서버와 클라이언트는 별도의 스레드에서 실행된다.
 * 따라서 테스트에서 트랜잭션을 시작하는 경우 웹 서버에서와 동일한 트랜잭션이 아니게 된다.
 * 서버 스레드에서 트랜잭션을 롤백할 수 없기 때문에 더 이상 테스트에서 @Transactional을 사용할 수 없게 된다.
 * 이 불편함에 대한 해결책은 수동으로 데이터를 삽입하고 삭제하는 것이다.
 */

// 실제 환경과 비슷하게 테스트하기 위해 임베디드 웹서버로 테스트 실행
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServerIntegrationTest {
	@Autowired
	private WebTestClient webClient;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	private static MockWebServer mockWebServer;

	/**
	 * OrderService와 함께 ExchangeRateClient가 SprinBean으로 등록되고 테스트코드가 실행됨
	 * ExchangeRateClientTest처럼 mockWebServer의 url을 properties에 지정하여
	 * ExchangeRateClient에 넣어준 후 exchangeRateClient를 주입하는 방식이 아님
	 * 따라서 ExchangeRateClient에 동적으로 url정보를 추가해줘야함
	 */
	@DynamicPropertySource
	public static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("exchange-rate-api.base-url", () -> mockWebServer.url("/").url().toString());
	}

	@BeforeAll
	public static void setupMockWebServer() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
	}

	// @Transactional을 쓸수 없으므로 대신 delete하기
	@AfterEach
	public void deleteEntities() {
		paymentRepository.deleteAll();
		orderRepository.deleteAll();
	}

	@Test
	public void createOrder() {
		webClient.post().uri("/order")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"amount\": \"EUR100.0\"}")
				.exchange()
				.expectStatus().isCreated();
	}

	@Test
	void payOrder() {
		Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(100.0), false);
		Long orderId = orderRepository.save(order).getId();

		webClient.post().uri("/order/{id}/payment", orderId)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"creditCardNumber\": \"4532756279624064\"}")
				.exchange()
				.expectStatus().isCreated();
	}

	@Test
	void getReceipt() {
		Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(100.0), true);
		Payment payment = new Payment(order, "4532756279624064");

		Long orderId = orderRepository.save(order).getId();
		paymentRepository.save(payment);

		// exchangeRateClient.getExchangeRate(eur, usd)에 대한 모의외부서버의 응답 지정하기
		mockWebServer.enqueue(
				new MockResponse().setResponseCode(200)
						.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.setBody("{\"conversion_rate\": 0.8412}")
		);

		webClient.get().uri("/order/{id}/receipt?currency=USD", orderId)
				.exchange()
				.expectStatus().isOk();
	}
}
