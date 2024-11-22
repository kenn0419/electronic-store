package com.kennn.electronicstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kennn.electronicstore.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
