package com.indrekjaadla.webshoporders.order;

import com.indrekjaadla.webshoporders.order.dto.OrderListResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional(readOnly = true)
    OrderListResponse getOrders() {
        return orderMapper.toListResponse(orderRepository.findAllWithItems());
    }
}
