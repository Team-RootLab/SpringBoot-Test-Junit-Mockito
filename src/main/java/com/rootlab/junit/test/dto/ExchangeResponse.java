package com.rootlab.junit.test.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExchangeResponse {
	@JsonAlias("conversion_rate")
	private BigDecimal conversionRate;
	private String result;
	@JsonAlias("error-type")
	private String errorType;
}
