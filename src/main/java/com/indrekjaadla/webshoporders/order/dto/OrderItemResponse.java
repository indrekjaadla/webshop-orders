package com.indrekjaadla.webshoporders.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        String name,
        int quantity,
        BigDecimal unitPrice
) {
}
