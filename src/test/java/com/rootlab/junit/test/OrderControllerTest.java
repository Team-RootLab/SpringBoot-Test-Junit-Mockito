package com.rootlab.junit.test;

import com.rootlab.junit.handler.GlobalExceptionHandler;
import com.rootlab.junit.test.controller.OrderController;
import com.rootlab.junit.test.dto.Receipt;
import com.rootlab.junit.test.entity.Order;
import com.rootlab.junit.test.entity.Payment;
import com.rootlab.junit.test.exception.OrderAlreadyPaid;
import com.rootlab.junit.test.service.OrderService;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest는 @Controller, @ControllerAdvice와 같은 Controller 관련 Bean만 scan하여 load한다.
// Controller가 의존하는 다른 Bean들은 @MockBean으로 가짜 객체를 만들어 주입시켜야 한다.
// @WebMvcTest, @MockBean은 통합테스트에 해당한다.

// @WebMvcTest: Web Layer(Controller, ControllerAdvice)만 생성하여 테스트 실행, @AutoConfigureMockMvc 포함
// @AutoConfigureMockMvc는 MockMvc를 inject해주는 역할을 함
// class 지정없이 @WebMvcTest로 실행시 Service에 의존하는 Controller에 Service Bean을 주입하지 못해
// (@Controller만 Bean으로 등록 @Service는 등록되지 않음) No qualifying bean of type 에러가 발생함

// @MockBean은 특정 빈을 Mock이 적용된 빈으로 등록한다.
// 그러므로 애플리케이션 컨텍스트가 갖는 빈이 달라져 새로운 컨텍스트를 생성하게 된다.
// @MockBean을 많이 사용하면 테스트가 느려질 수 있고 캐싱된 애플리케이션 컨텍스트의 수를 증가시킨다.
// 출처: https://mangkyu.tistory.com/244

// 이전 코드에 포함된 GlobalExceptionHandler에 의해 테스트 오류 발생
// 이 테스트를 실행할때에는 exclude하도록 함
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
		Order order = new Order(1L, LocalDateTime.now(), BigDecimal.valueOf(100.0), false);
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
		Receipt receipt = new Receipt(LocalDateTime.now(), "4532756279624064", BigDecimal.valueOf(100.0));
		// stub
		when(orderService.getReceipt(eq(1L))).thenReturn(receipt);
		// when & then
		mockMvc.perform(get("/order/{id}/receipt", 1L))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.date").isNotEmpty())
				.andExpect(jsonPath("$.creditCardNumber").value("4532756279624064"))
				.andExpect(jsonPath("$.amount").value(100.0));
	}

}