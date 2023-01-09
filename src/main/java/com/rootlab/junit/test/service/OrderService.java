package com.rootlab.junit.test.service;

import com.rootlab.junit.test.dto.Receipt;
import com.rootlab.junit.test.entity.Order;
import com.rootlab.junit.test.entity.Payment;
import com.rootlab.junit.test.exception.OrderAlreadyPaid;
import com.rootlab.junit.test.repository.OrderRepository;
import com.rootlab.junit.test.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;

	public Payment pay(Long orderId, String creditCardNumber) {
		Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
		if (order.isPaid()) {
			throw new OrderAlreadyPaid();
		}
		orderRepository.save(order.markPaid());
		return paymentRepository.save(new Payment(order, creditCardNumber));
	}

	public Receipt getReceipt(Long orderId) {
		Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(EntityNotFoundException::new);
		Order order = payment.getOrder();
		return new Receipt(order.getDate(), payment.getCreditCardNumber(), order.getAmount());
	}

}
