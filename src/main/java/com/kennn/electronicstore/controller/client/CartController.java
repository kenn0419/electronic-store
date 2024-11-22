package com.kennn.electronicstore.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import com.kennn.electronicstore.domain.Cart;
import com.kennn.electronicstore.domain.CartDetail;
import com.kennn.electronicstore.domain.User;
import com.kennn.electronicstore.domain.dto.OrderDTO;
import com.kennn.electronicstore.service.CartDetailService;
import com.kennn.electronicstore.service.CartService;
import com.kennn.electronicstore.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CartController {
    private ProductService productService;
    private CartService cartService;
    private CartDetailService cartDetailService;

    public CartController(ProductService productService, CartService cartService, CartDetailService cartDetailService) {
        this.productService = productService;
        this.cartService = cartService;
        this.cartDetailService = cartDetailService;
    }

    @GetMapping("/cart")
    public String getCartPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = new User();
        user.setId((long) session.getAttribute("id"));
        Cart cart = this.cartService.findByUser(user);
        if (cart != null) {
            double totalPriceCart = 0;
            for (CartDetail cartDetail : cart.getCartDetails()) {
                totalPriceCart += cartDetail.getPrice() * cartDetail.getQuantity();
            }

            model.addAttribute("totalPriceCart", totalPriceCart);
            model.addAttribute("cart", cart);
        } else {
            model.addAttribute("emptyCart", "Giỏ hàng chưa có sản phẩm nào");
        }
        return "client/page/cart/index";
    }

    @PostMapping("/confirm-checkout")
    public String transferCheckout(@ModelAttribute("cart") Cart cart) {
        List<CartDetail> cartDetails = cart == null ? new ArrayList<>() : cart.getCartDetails();

        this.cartDetailService.updateQuantityCartDetail(cartDetails);
        return "redirect:/checkout";
    }

    @GetMapping("/checkout")
    public String getCheckoutPage(Model model, HttpServletRequest request) {
        OrderDTO order = new OrderDTO();
        HttpSession session = request.getSession(false);
        User user = new User();
        user.setId((long) session.getAttribute("id"));

        Cart cart = this.cartService.findByUser(user);

        model.addAttribute("cart", cart);
        model.addAttribute("order", order);
        return "client/page/cart/checkout";
    }

    @PostMapping("/checkout")
    public String orderProducts(@Valid @ModelAttribute("order") OrderDTO orderDTO, BindingResult result, Model model,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = new User();
        user.setId((long) session.getAttribute("id"));

        Cart cart = this.cartService.findByUser(user);
        if (result.hasErrors()) {

            model.addAttribute("cart", cart);
            return "client/page/cart/checkout";
        }

        this.productService.handleOrderProducts(user, cart, session, orderDTO);
        return "redirect:/thank-you";
    }

    @GetMapping("/thank-you")
    public String getThankYouPage() {
        return "client/page/cart/thankyou";
    }

}
