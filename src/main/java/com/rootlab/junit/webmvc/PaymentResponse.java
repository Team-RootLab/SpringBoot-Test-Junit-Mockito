package com.rootlab.junit.webmvc;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class PaymentResponse {
	private final Long orderId;
	private final String creditCardNumber;
}
