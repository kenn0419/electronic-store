package com.kennn.electronicstore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kennn.electronicstore.domain.CartDetail;
import com.kennn.electronicstore.repository.CartDetailRepository;

@Service
public class CartDetailService {
    private CartDetailRepository cartDetailRepository;

    public CartDetailService(CartDetailRepository cartDetailRepository) {
        this.cartDetailRepository = cartDetailRepository;
    }

    public Optional<CartDetail> findById(long id) {
        return this.cartDetailRepository.findById(id);
    }

    public void updateQuantityCartDetail(List<CartDetail> cartDetails) {
        for (CartDetail cartDetail : cartDetails) {
            Optional<CartDetail> optionalCartDetail = this.findById(cartDetail.getId());
            if (optionalCartDetail.isPresent()) {
                CartDetail updateCartDetail = optionalCartDetail.get();

                updateCartDetail.setQuantity(cartDetail.getQuantity());

                this.cartDetailRepository.save(updateCartDetail);
            }
        }
    }
}
