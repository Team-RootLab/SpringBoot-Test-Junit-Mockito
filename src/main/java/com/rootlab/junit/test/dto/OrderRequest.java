package com.rootlab.junit.test.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.money.MonetaryAmount;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class OrderRequest {
	@NotNull
	private MonetaryAmount amount;
}
