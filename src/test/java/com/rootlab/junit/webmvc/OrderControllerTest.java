package com.rootlab.junit.webmvc;

import com.rootlab.junit.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest는 @Controller, @ControllerAdvice와 같은 Controller 관련 Bean만 scan하여 load한다.
// Controller가 의존하는 다른 Bean들은 @MockBean으로 가짜 객체를 만들어 주입해준다.
// @WebMvcTest, @MockBean은 통합테스트에 해당한다.

// @MockBean은 특정 빈을 Mock이 적용된 빈으로 등록한다.
// 그러므로 애플리케이션 컨텍스트가 갖는 빈이 달라져 새로운 컨텍스트를 생성하게 된다.
// @MockBean을 많이 사용하면 테스트가 느려질 수 있고 캐싱된 애플리케이션 컨텍스트의 수를 증가시킨다.
// 출처: https://mangkyu.tistory.com/244

//@WebMvcTest(OrderController.class)
@WebMvcTest(
		controllers = OrderController.class,
		excludeFilters = {
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						classes = GlobalExceptionHandler.class
				)
		}
)
class OrderControllerTest {
	@MockBean
	private OrderService orderService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void payOrder() throws Exception {
		// given
		Order order = new Order(1L, LocalDateTime.now(), 100.0, false);
		Payment payment = new Payment(1000L, order, "4532756279624064");
		// stub
		when(orderService.pay(eq(1L), eq("4532756279624064"))).thenReturn(payment);
		// when & then
		mockMvc.perform(post("/order/{id}/payment", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"creditCardNumber\": \"4532756279624064\"}"))
				.andExpect(status().isCreated())
				.andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/order/1/receipt"));
	}

	@Test
	public void paymentFailsWhenOrderIsNotFound() throws Exception {
		// stub
		when(orderService.pay(eq(1L), any())).thenThrow(EntityNotFoundException.class);
		// when & then
		mockMvc.perform(post("/order/{id}/payment", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"creditCardNumber\": \"4532756279624064\"}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void paymentFailsWhenCreditCardNumberNotGiven() throws Exception {
		// when & then
		mockMvc.perform(post("/order/{id}/payment", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void cannotPayAlreadyPaidOrder() throws Exception {
		// stub
		when(orderService.pay(eq(1L), any())).thenThrow(OrderAlreadyPaid.class);
		// when & then
		mockMvc.perform(post("/order/{id}/payment", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"creditCardNumber\": \"4532756279624064\"}"))
				.andExpect(status().isMethodNotAllowed());
	}

	@Test
	void getReceiptForOrder() throws Exception {
		// given
		Receipt receipt = new Receipt(LocalDateTime.now(), "4532756279624064", 100.0);
		// stub
		when(orderService.getReceipt(eq(1L))).thenReturn(receipt);
		// when & then
		mockMvc.perform(get("/order/{id}/receipt", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.date").isNotEmpty())
				.andExpect(jsonPath("$.creditCardNumber").value("4532756279624064"))
				.andExpect(jsonPath("$.amount").value(100.0));
	}

}