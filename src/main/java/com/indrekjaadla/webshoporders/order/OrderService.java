package com.indrekjaadla.webshoporders.order;

import com.indrekjaadla.webshoporders.order.dto.CreateOrderItemRequest;
import com.indrekjaadla.webshoporders.order.dto.CreateOrderRequest;
import com.indrekjaadla.webshoporders.order.dto.OrderListResponse;
import com.indrekjaadla.webshoporders.order.dto.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    OrderResponse createOrder(CreateOrderRequest request) {
        List<OrderItem> items = new ArrayList<>();

        for (int i = 0; i < request.items().size(); i++) {
            CreateOrderItemRequest itemRequest = request.items().get(i);
            items.add(new OrderItem(
                    i + 1,
                    itemRequest.name().trim(),
                    itemRequest.quantity(),
                    itemRequest.unitPrice()
            ));
        }

        return orderMapper.toResponse(orderRepository.saveAndFlush(new Order(OrderStatus.NEW, items)));
    }
}
