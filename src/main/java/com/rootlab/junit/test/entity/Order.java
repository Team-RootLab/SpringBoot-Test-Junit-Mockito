package com.rootlab.junit.test.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Order {
	@Id
	@GeneratedValue
	private Long id;
	@NonNull
	private LocalDateTime date;
	@NonNull
	private BigDecimal amount;
	@NonNull
	private Boolean paid;

	public boolean isPaid() {
		return paid;
	}

	public Order markPaid() {
		paid = true;
		return this;
	}
}
