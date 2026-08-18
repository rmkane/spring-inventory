package org.acme.inventory.dto.order;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

import org.acme.inventory.domain.OrderStatus;

// spotless:off
@Schema(description = "Payload for creating or updating an order")
public record OrderRequest(
        @NotNull
        @Schema(description = "Customer placing the order")
        UUID customerId,

        @Schema(description = "Order status. Defaults to PENDING on create.", example = "PENDING")
        OrderStatus status,

        @NotEmpty
        @Schema(description = "Order line items")
        List<@Valid OrderItemRequest> items) {
}
// spotless:on
