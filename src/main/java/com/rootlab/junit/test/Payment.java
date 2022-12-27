package com.rootlab.junit.test;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@NonNull
	private Order order;

	@NonNull
	private String creditCardNumber;
}
