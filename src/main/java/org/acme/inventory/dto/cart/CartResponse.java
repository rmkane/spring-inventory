package org.acme.inventory.dto.cart;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

// spotless:off
@Schema(description = "A shopping cart with catalog-priced line items")
public record CartResponse(
        @Schema(description = "Cart id", example = "1a2b3c4d-5e6f-7081-92a3-b4c5d6e7f809")
        UUID id,

        @Schema(description = "Customer who owns the cart", example = "0c1e4a2a-8b3f-4d9e-9a1c-2f7b6d5e4c3b")
        UUID customerId,

        @Schema(description = "Sum of line totals at current catalog prices", example = "59.98")
        BigDecimal total,

        @Schema(description = "When the cart was created", example = "2026-03-15T14:30:00Z")
        OffsetDateTime createdAt,

        @Schema(description = "When the cart was last updated", example = "2026-03-16T09:12:00Z")
        OffsetDateTime updatedAt,

        @Schema(description = "Cart line items")
        List<CartItemResponse> items) {
}
// spotless:on
