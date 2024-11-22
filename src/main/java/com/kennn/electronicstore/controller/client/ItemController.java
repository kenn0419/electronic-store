package com.kennn.electronicstore.controller.client;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.kennn.electronicstore.domain.Order;
import com.kennn.electronicstore.domain.Product;
import com.kennn.electronicstore.domain.User;
import com.kennn.electronicstore.service.ProductService;
import com.kennn.electronicstore.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {
    private ProductService productService;
    private UserService userService;

    public ItemController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/product/{id}")
    public String getDetailProduct(@PathVariable long id, Model model) {
        Optional<Product> optionalProduct = this.productService.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            model.addAttribute("product", product);
        }
        return "client/page/product/detail";
    }

    @GetMapping("/products")
    public String getProductListPage(Model model) {
        List<Product> products = this.productService.findAll();

        model.addAttribute("products", products);
        return "client/page/product/list";
    }

    @GetMapping("/your-order")
    public String getOrderHistoryPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        Optional<User> optionalUser = this.userService.findById((long) session.getAttribute("id"));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            List<Order> orders = user.getOrders();
            if (orders.size() > 0) {
                model.addAttribute("orders", orders);
            } else {
                model.addAttribute("emptyOrder", "Chưa có đơn đặt hàng nào");
            }
        }
        return "client/page/order/index";
    }

}
