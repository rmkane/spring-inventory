package org.acme.inventory.dto.cart;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

// spotless:off
@Schema(description = "Payload for creating or updating a cart")
public record CartRequest(
        @NotNull
        @Schema(description = "Customer who owns the cart")
        UUID customerId,

        @NotNull
        @Schema(description = "Cart line items")
        List<@Valid CartItemRequest> items) {
}
// spotless:on
