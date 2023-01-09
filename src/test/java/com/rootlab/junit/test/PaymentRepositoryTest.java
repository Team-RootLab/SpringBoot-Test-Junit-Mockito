package com.rootlab.junit.test;

import com.rootlab.junit.test.entity.Order;
import com.rootlab.junit.test.entity.Payment;
import com.rootlab.junit.test.repository.PaymentRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.PersistenceException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// H2 데이터베이스가 아닌 다른 Real DB를 사용하는 경우 아래와 같이 설정
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@TestPropertySource(properties = {
//		"spring.datasource.url=jdbc:tc:postgresql:13.2-alpine://payment"
//})
@DataJpaTest
@Disabled
class PaymentRepositoryTest {
	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void existingPaymentCanBeFound() {
		Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(100.0), true);
		Payment payment = new Payment(order, "4532756279624064");
		Long orderId = entityManager.persist(order).getId();
		entityManager.persist(payment);
		Optional<Payment> savedPayment = paymentRepository.findByOrderId(orderId);
		assertThat(savedPayment).isPresent();
		assertThat(savedPayment.get().getOrder().getPaid()).isTrue();
	}

	@Test
	void paymentsAreUniquePerOrder() {
		Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(100.0), true);
		Payment first = new Payment(order, "4532756279624064");
		Payment second = new Payment(order, "4716327217780406");
		entityManager.persist(order);
		entityManager.persist(first);
		assertThrows(PersistenceException.class, () -> entityManager.persistAndFlush(second));
	}

	@Test
	@Sql("/multiple-payments.sql")
	void findPaymentsAfterDate() {
		List<Payment> payments = paymentRepository.findAllAfter(LocalDateTime.now().minusDays(1));
		assertThat(payments).extracting("order.id").containsOnly(1L);
	}

	@Test
	@Sql("/multiple-payments.sql")
	void findPaymentsByCreditCard() {
		List<Payment> payments = paymentRepository.findByCreditCardNumber("4532756279624064");
		assertThat(payments).extracting("order.id").containsOnly(1L);
	}
}
