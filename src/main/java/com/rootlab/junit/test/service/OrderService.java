package com.rootlab.junit.test.service;

import com.rootlab.junit.test.clients.exchange.ExchangeRateClient;
import com.rootlab.junit.test.dto.Receipt;
import com.rootlab.junit.test.entity.Order;
import com.rootlab.junit.test.entity.Payment;
import com.rootlab.junit.test.exception.OrderAlreadyPaid;
import com.rootlab.junit.test.repository.OrderRepository;
import com.rootlab.junit.test.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final ExchangeRateClient exchangeRateClient;

	public Order createOrder(MonetaryAmount amount) {
		Order order = new Order(LocalDateTime.now(), amount.getNumber().numberValue(BigDecimal.class), false);
		return orderRepository.save(order);
	}

	public Order getOrder(Long orderId) {
		return orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
	}

	public Payment pay(Long orderId, String creditCardNumber) {
		Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
		if (order.isPaid()) {
			throw new OrderAlreadyPaid();
		}
		orderRepository.save(order.markPaid());
		return paymentRepository.save(new Payment(order, creditCardNumber));
	}

//	public Receipt getReceipt(Long orderId) {
//		Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(EntityNotFoundException::new);
//		Order order = payment.getOrder();
//		return new Receipt(order.getDate(), payment.getCreditCardNumber(), order.getAmount());
//	}

	public Receipt getReceipt(Long orderId, CurrencyUnit currency) {
		Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(EntityNotFoundException::new);

		BigDecimal rate = exchangeRateClient.getExchangeRate(Monetary.getCurrency("EUR"), currency);

		BigDecimal amount = payment.getOrder().getAmount();
		MonetaryAmount convertedAmount = Money.of(amount.multiply(rate), currency);

		return new Receipt(payment.getOrder().getDate(), payment.getCreditCardNumber(), convertedAmount);
	}

}
