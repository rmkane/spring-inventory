package org.acme.inventory.domain;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record Order(
        UUID id,
        UUID customerId,
        OrderStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime paidAt,
        OffsetDateTime shippedAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt,
        List<OrderItem> items) {
}
