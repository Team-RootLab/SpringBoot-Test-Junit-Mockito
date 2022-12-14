package com.rootlab.junit.test;

import com.rootlab.junit.test.entity.Order;
import com.rootlab.junit.test.entity.Payment;
import com.rootlab.junit.test.repository.OrderRepository;
import com.rootlab.junit.test.repository.PaymentRepository;
import com.rootlab.junit.test.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Spring Boot Container가 테스트 시에 필요하고 Bean이 Container에 존재한다면
// @MockBean을 사용하고 그게 아닌 경우에는 @Mock을 사용한다.
// @SpringBookTest는 테스트 시간이 길어지게 되므로 unit test를 이용하는 것이 좋음

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	// Test class의 필드외에도 아래와 같이 Test method의 매개변수에도 사용가능하다.
	// void payOrderTest(@Mock OrderRepository orderRepository)

	@Mock
	private static OrderRepository orderRepository;
	@Mock
	private static PaymentRepository paymentRepository;
	@InjectMocks
	private static OrderService orderService;

//	private AutoCloseable closeable;

	// @ExtendWith(MockitoExtension.class) 선언시 아래와 같은 방식 없이도
	// @Mock, @InjectMocks를 활용해 Mock객체를 주입할 수 있음
//	@BeforeAll
//	private static void setupService() {
//		orderRepository = mock(OrderRepository.class);
//		paymentRepository = mock(PaymentRepository.class);
//		orderService = new OrderService(orderRepository, paymentRepository);
//	}


	// Mockito5에서는 @ExtendWith(MockitoExtension.class) 선언시
	// 아래와 같은 Mock closing 작업을 자동화시킬 수 있다.
//	@BeforeEach
//	void initService() {
//		closeable = MockitoAnnotations.openMocks(this);
//	}
//
//	@AfterEach
//	void closeService() throws Exception {
//		closeable.close();
//	}

	@Test
	public void payOrderTest() {
		// given
		Order order = new Order(1L, LocalDateTime.now(), BigDecimal.valueOf(100.0), false);
		// stub
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(paymentRepository.save(any())).then(returnsFirstArg()); // input을 그대로 리턴
		// when
		Payment payment = orderService.pay(1L, "4532756279624064");
		// then
		assertThat(payment.getOrder().isPaid()).isTrue();
		assertThat(payment.getCreditCardNumber()).isEqualTo("4532756279624064");
	}
}