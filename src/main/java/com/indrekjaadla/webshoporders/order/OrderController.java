package com.indrekjaadla.webshoporders.order;

import com.indrekjaadla.webshoporders.order.dto.OrderListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
class OrderController {

    private final OrderService orderService;

    OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    OrderListResponse getOrders() {
        return orderService.getOrders();
    }
}
