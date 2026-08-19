package org.acme.inventory.repository.mapper.order;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.acme.inventory.domain.Order;
import org.acme.inventory.domain.OrderItem;
import org.acme.inventory.domain.OrderStatus;

public record OrderRow(
        UUID id,
        UUID customerId,
        OrderStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime paidAt,
        OffsetDateTime shippedAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt) {

    public Order toOrder(List<OrderItem> items) {
        return new Order(
                id,
                customerId,
                status,
                createdAt,
                updatedAt,
                paidAt,
                shippedAt,
                completedAt,
                cancelledAt,
                List.copyOf(items));
    }
}
