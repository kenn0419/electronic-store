package com.kennn.electronicstore.service;

import java.util.List;
import java.util.Optional;

import java.util.Map;
import java.util.HashMap;
import org.springframework.stereotype.Service;

import com.kennn.electronicstore.domain.Order;
import com.kennn.electronicstore.domain.OrderDetail;
import com.kennn.electronicstore.domain.OrderStatusEnum;
import com.kennn.electronicstore.repository.OrderDetailRepository;
import com.kennn.electronicstore.repository.OrderRepository;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private OrderDetailRepository orderDetailRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public List<Order> findAll() {
        return this.orderRepository.findAll();
    }

    public Optional<Order> findById(long id) {
        return this.orderRepository.findById(id);
    }

    public Order save(Order order) {
        return this.orderRepository.save(order);
    }

    public void remove(Order order) {
        List<OrderDetail> orderDetails = order.getOrderDetails();
        if (orderDetails != null) {
            for (OrderDetail orderDetail : orderDetails) {
                this.orderDetailRepository.delete(orderDetail);
            }
            this.orderRepository.delete(order);
        }
    }

    public Map<String, Object> cancelOrder(Order order) {
        Map<String, Object> response = new HashMap<>();

        if (order.getStatus() == OrderStatusEnum.SHIPPED || order.getStatus() == OrderStatusEnum.DELIVERED) {
            response.put("status", false);
            response.put("message", "Đơn hàng đã trên đường giao không thể huỷ");
        } else if (order.getStatus() == OrderStatusEnum.RETURNED) {
            response.put("status", false);
            response.put("message", "Đơn hàng đã được trả về không thể huỷ");
        } else {
            order.setStatus(OrderStatusEnum.CANCELLED);

            this.orderRepository.save(order);

            response.put("status", true);
            response.put("message", "Đã gửi yêu cầu huỷ đơn hàng");
        }
        return response;
    }
}
