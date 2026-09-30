package com.indrekjaadla.webshoporders.order.dto;

import com.indrekjaadla.webshoporders.order.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        OrderStatus status,
        Instant createdAt,
        List<OrderItemResponse> items
) {
}
