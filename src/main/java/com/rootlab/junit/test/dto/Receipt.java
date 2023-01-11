package com.rootlab.junit.test.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class Receipt {
	private final LocalDateTime date;
	private final String creditCardNumber;
	private final MonetaryAmount amount;
}
