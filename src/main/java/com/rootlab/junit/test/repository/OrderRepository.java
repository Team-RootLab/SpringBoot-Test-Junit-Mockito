package com.rootlab.junit.test.repository;

import com.rootlab.junit.test.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
