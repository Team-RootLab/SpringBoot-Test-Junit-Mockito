package com.rootlab.junit.repository;

import com.rootlab.junit.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
