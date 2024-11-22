package com.kennn.electronicstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kennn.electronicstore.domain.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

}
