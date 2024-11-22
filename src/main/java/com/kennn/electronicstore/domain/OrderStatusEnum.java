package com.kennn.electronicstore.domain;

public enum OrderStatusEnum {
    PENDING, // Order is created but not yet processed
    PROCESSING, // Order is being processed
    SHIPPED, // Order has been shipped
    DELIVERED, // Order has been delivered to the customer
    CANCELLED, // Order has been cancelled
    RETURNED // Order has been returned
}
