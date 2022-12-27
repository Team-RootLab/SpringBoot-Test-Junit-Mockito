package com.rootlab.junit.webmvc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "web_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
	@Id
	@GeneratedValue
	private Long id;
	@NonNull
	private LocalDateTime date;
	@NonNull
	private Double amount;
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
