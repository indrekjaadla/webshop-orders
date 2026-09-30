package com.indrekjaadla.webshoporders.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items ORDER BY o.createdAt DESC, o.id")
    List<Order> findAllWithItems();
}
