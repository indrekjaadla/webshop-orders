package com.indrekjaadla.webshoporders.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty
        @Size(max = 100)
        List<@Valid CreateOrderItemRequest> items
) {
}
