package com.indrekjaadla.webshoporders.order.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateOrderItemRequest(
        @NotBlank
        @Size(max = 255)
        String name,

        @NotNull
        @Min(1)
        @Max(100)
        Integer quantity,

        @NotNull
        @DecimalMin("0.01")
        @DecimalMax("2500.00")
        @Digits(integer = 4, fraction = 2)
        BigDecimal unitPrice
) {
}
