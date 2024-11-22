package com.kennn.electronicstore.controller.api;

import org.springframework.web.bind.annotation.RestController;

import com.kennn.electronicstore.domain.Order;
import com.kennn.electronicstore.service.OrderService;

import java.util.Optional;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class OrderApiController {

    private OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/api/cancel-order/{id}")
    public Map<String, Object> cancelOrder(@PathVariable long id) {
        Optional<Order> optionalOrder = this.orderService.findById(id);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            Map<String, Object> response = this.orderService.cancelOrder(order);
            return response;
        }
        return null;
    }

}
