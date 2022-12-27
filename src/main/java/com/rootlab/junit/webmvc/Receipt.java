package com.rootlab.junit.webmvc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class Receipt {
	private final LocalDateTime date;
	private final String creditCardNumber;
	private final Double amount;
}
