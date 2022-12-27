package com.rootlab.junit.test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class Receipt {
	private final LocalDateTime date;
	private final String creditCardNumber;
	private final BigDecimal amount;
}
