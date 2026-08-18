package org.acme.inventory.dto.order;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.acme.inventory.domain.OrderStatus;

public record OrderResponse(
        UUID id,
        UUID customerId,
        OrderStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime paidAt,
        OffsetDateTime shippedAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt,
        List<OrderItemResponse> items) {
}
