package org.acme.inventory.dto.order;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

import org.acme.inventory.domain.OrderStatus;

// spotless:off
@Schema(description = "An order with snapshotted line items")
public record OrderResponse(
        @Schema(description = "Order id", example = "9d8c7b6a-5e4d-3c2b-1a09-87654321fedc")
        UUID id,

        @Schema(description = "Customer who placed the order", example = "0c1e4a2a-8b3f-4d9e-9a1c-2f7b6d5e4c3b")
        UUID customerId,

        @Schema(description = "Order status", example = "PENDING")
        OrderStatus status,

        @Schema(description = "When the order was created", example = "2026-03-15T14:30:00Z")
        OffsetDateTime createdAt,

        @Schema(description = "When the order was last updated", example = "2026-03-16T09:12:00Z")
        OffsetDateTime updatedAt,

        @Schema(description = "When the order was paid", example = "2026-03-15T15:00:00Z", nullable = true)
        OffsetDateTime paidAt,

        @Schema(description = "When the order was shipped", example = "2026-03-16T10:00:00Z", nullable = true)
        OffsetDateTime shippedAt,

        @Schema(description = "When the order was completed", example = "2026-03-18T12:00:00Z", nullable = true)
        OffsetDateTime completedAt,

        @Schema(description = "When the order was cancelled", nullable = true)
        OffsetDateTime cancelledAt,

        @Schema(description = "Order line items")
        List<OrderItemResponse> items) {
}
// spotless:on
