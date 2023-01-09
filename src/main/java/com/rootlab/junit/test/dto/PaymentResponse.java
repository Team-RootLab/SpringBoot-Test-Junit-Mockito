package com.rootlab.junit.test.dto;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class PaymentResponse {
	private final Long orderId;
	private final String creditCardNumber;
}
