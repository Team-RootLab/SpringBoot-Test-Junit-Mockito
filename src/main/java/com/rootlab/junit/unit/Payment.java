package com.rootlab.junit.unit;

import lombok.*;

import javax.persistence.*;

@Entity(name = "unit_payment")
@Data
@RequiredArgsConstructor
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
