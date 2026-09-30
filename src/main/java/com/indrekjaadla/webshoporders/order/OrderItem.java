package com.indrekjaadla.webshoporders.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int lineNumber;
    private String name;
    private int quantity;
    private BigDecimal unitPrice;

    protected OrderItem() {
    }

    OrderItem(int lineNumber, String name, int quantity, BigDecimal unitPrice) {
        this.lineNumber = lineNumber;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    int getLineNumber() {
        return lineNumber;
    }

    String getName() {
        return name;
    }

    int getQuantity() {
        return quantity;
    }

    BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
