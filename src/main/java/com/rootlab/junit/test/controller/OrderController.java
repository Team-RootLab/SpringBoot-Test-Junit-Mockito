package com.rootlab.junit.test.controller;

import com.rootlab.junit.test.dto.*;
import com.rootlab.junit.test.entity.Order;
import com.rootlab.junit.test.service.OrderService;
import com.rootlab.junit.test.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.money.CurrencyUnit;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	@PostMapping("/order")
	public ResponseEntity<Order> createOrder(
			@RequestBody @Valid OrderRequest orderRequest,
			UriComponentsBuilder uriComponentsBuilder) {

		Order order = orderService.createOrder(orderRequest.getAmount());
		URI location = uriComponentsBuilder.path("/order/{id}").buildAndExpand(order.getId()).toUri();
		return ResponseEntity.created(location).body(order);
	}

	@GetMapping("/order/{id}")
	public ResponseEntity<Order> getOrder(@PathVariable("id") Long orderId) {
		Order order = orderService.getOrder(orderId);
		return ResponseEntity.ok().body(order);
	}

	@PostMapping("/order/{id}/payment")
	public ResponseEntity<PaymentResponse> pay(
			@PathVariable("id") Long orderId,
			@RequestBody @Valid PaymentRequest paymentRequest,
			UriComponentsBuilder uriComponentsBuilder
	) {
		Payment payment = orderService.pay(orderId, paymentRequest.getCreditCardNumber());
		URI location = uriComponentsBuilder.path("/order/{id}/receipt").buildAndExpand(orderId).toUri();
		PaymentResponse response = new PaymentResponse(payment.getOrder().getId(), payment.getCreditCardNumber());
		return ResponseEntity.created(location).body(response);
	}

	@GetMapping("/order/{id}/receipt")
	public ResponseEntity<Receipt> getReceipt(
			@PathVariable("id") Long orderId,
			@RequestParam(value = "currency", defaultValue = "EUR") CurrencyUnit currency
	) {
		Receipt receipt = orderService.getReceipt(orderId, currency);
		return ResponseEntity.ok().body(receipt);
	}

}
