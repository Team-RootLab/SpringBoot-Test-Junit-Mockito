package com.rootlab.junit.test;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class PaymentResponse {
	private final Long orderId;
	private final String creditCardNumber;
}
