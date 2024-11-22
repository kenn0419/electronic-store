package com.kennn.electronicstore.controller.api;

import org.springframework.web.bind.annotation.RestController;

import com.kennn.electronicstore.domain.Cart;
import com.kennn.electronicstore.domain.User;
import com.kennn.electronicstore.domain.dto.CartDTO;
import com.kennn.electronicstore.service.CartService;
import com.kennn.electronicstore.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class CartApiController {
    private final ProductService productService;
    private final CartService cartService;

    public CartApiController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @PostMapping("/api/add-to-cart")
    public ResponseEntity<Integer> postMethodName(@RequestBody() CartDTO cart, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        this.productService.handleAddToCart(email, cart.getProductId(), session, cart.getQuantity());

        int sum = (int) session.getAttribute("sum");
        return ResponseEntity.ok().body(sum);
    }

    @PostMapping("/api/delete-from-cart-product/{id}")
    public ResponseEntity<Integer> deleteProductFromCart(@PathVariable long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = new User();
        user.setId((long) session.getAttribute("id"));
        Cart cart = this.cartService.findByUser(user);
        int sum = this.productService.handleRemoveProductFromCart(id, cart, session);
        return ResponseEntity.ok().body(sum);
    }
}
