package org.acme.inventory.dto.cart;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

// spotless:off
@Schema(description = "A product line in a cart")
public record CartItemResponse(
        @Schema(description = "Product id", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
        UUID productId,

        @Schema(description = "Product name", example = "Wireless mouse")
        String productName,

        @Schema(description = "Quantity in the cart", example = "2")
        int quantity,

        @Schema(description = "Current catalog unit price", example = "29.99")
        BigDecimal unitPrice,

        @Schema(description = "Quantity times unit price", example = "59.98")
        BigDecimal lineTotal,

        @Schema(description = "When the line was added", example = "2026-03-15T14:30:00Z")
        OffsetDateTime addedAt,

        @Schema(description = "When the line was last updated", example = "2026-03-16T09:12:00Z")
        OffsetDateTime updatedAt) {
}
// spotless:on
