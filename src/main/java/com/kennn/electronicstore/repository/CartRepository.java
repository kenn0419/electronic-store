package com.kennn.electronicstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kennn.electronicstore.domain.Cart;
import com.kennn.electronicstore.domain.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);
}
