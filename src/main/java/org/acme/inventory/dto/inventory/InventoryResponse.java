package org.acme.inventory.dto.inventory;

import java.time.OffsetDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

// spotless:off
@Schema(description = "Stock quantities for a product")
public record InventoryResponse(
        @Schema(description = "Product id", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
        UUID productId,

        @Schema(description = "Quantity on hand", example = "100")
        int quantityOnHand,

        @Schema(description = "Quantity reserved for carts and orders", example = "5")
        int quantityReserved,

        @Schema(description = "When inventory was last updated", example = "2026-03-16T09:12:00Z")
        OffsetDateTime updatedAt) {
}
// spotless:on
