package com.indrekjaadla.webshoporders.order;

import com.indrekjaadla.webshoporders.order.dto.OrderItemResponse;
import com.indrekjaadla.webshoporders.order.dto.OrderListResponse;
import com.indrekjaadla.webshoporders.order.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class OrderMapper {

    OrderListResponse toListResponse(List<Order> orders) {
        return new OrderListResponse(orders.stream().map(this::toResponse).toList());
    }

    OrderResponse toResponse(Order order) {
        return new OrderResponse(order.getId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getItems().stream().map(this::toItemResponse).toList());
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(item.getName(), item.getQuantity(), item.getUnitPrice());
    }
}
