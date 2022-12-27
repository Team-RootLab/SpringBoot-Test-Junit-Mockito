package com.rootlab.junit.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
	@Id
	@GeneratedValue
	private Long id;

	@OneToOne
	@NonNull
	private Order order;

	@NonNull
	private String creditCardNumber;

}
