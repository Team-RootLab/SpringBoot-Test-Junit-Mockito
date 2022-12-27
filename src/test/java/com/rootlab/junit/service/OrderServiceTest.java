package com.rootlab.junit.service;

import com.rootlab.junit.domain.Order;
import com.rootlab.junit.domain.Payment;
import com.rootlab.junit.repository.OrderRepository;
import com.rootlab.junit.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// @SpringBootTest의 경우 @MockBean을 이용해 context에 Mock Bean을 load할 수 있음
// 하지만 @SpringBookTest는 테스트 시간이 길어지게 되므로 unit test를 이용하는 것이 좋음
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
	@Mock
	private static OrderRepository orderRepository;
	@Mock
	private static PaymentRepository paymentRepository;
	@InjectMocks
	private static OrderService orderService;

	// @ExtendWith(MockitoExtension.class) 선언시 아래와 같은 방식 없이도
	// @Mock, @InjectMocks를 활용해 Mock객체를 주입할 수 있음

//	@BeforeAll
//	private static void setupService() {
//		orderRepository = mock(OrderRepository.class);
//		paymentRepository = mock(PaymentRepository.class);
//		orderService = new OrderService(orderRepository, paymentRepository);
//	}

	@Test
	public void payOrderTest() {
		// given
		Order order = new Order(1L, false);
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