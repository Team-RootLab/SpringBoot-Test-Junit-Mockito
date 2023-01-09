package com.rootlab.junit.test.controller;

import com.rootlab.junit.test.service.OrderService;
import com.rootlab.junit.test.dto.PaymentRequest;
import com.rootlab.junit.test.dto.PaymentResponse;
import com.rootlab.junit.test.dto.Receipt;
import com.rootlab.junit.test.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

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
	public ResponseEntity<Receipt> getReceipt(@PathVariable("id") Long orderId) {
		Receipt receipt = orderService.getReceipt(orderId);
		return ResponseEntity.ok().body(receipt);
	}

}
