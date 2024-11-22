package com.kennn.electronicstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kennn.electronicstore.domain.Cart;
import com.kennn.electronicstore.domain.CartDetail;
import com.kennn.electronicstore.domain.Product;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    CartDetail findByCartAndProduct(Cart cart, Product product);
}
