package org.acme.inventory.dto.product;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

// spotless:off
@Schema(description = "A product with current inventory quantities")
public record ProductResponse(
        @Schema(description = "Product id", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
        UUID id,

        @Schema(description = "Product name", example = "Wireless mouse")
        String name,

        @Schema(description = "Optional product description", example = "Ergonomic wireless mouse")
        String description,

        @Schema(description = "Unit price", example = "29.99")
        BigDecimal price,

        @Schema(description = "Quantity on hand", example = "100")
        int quantityOnHand,

        @Schema(description = "Quantity reserved for carts and orders", example = "5")
        int quantityReserved,

        @Schema(description = "When the product was created", example = "2026-03-15T14:30:00Z")
        OffsetDateTime createdAt,

        @Schema(description = "When the product was last updated", example = "2026-03-16T09:12:00Z")
        OffsetDateTime updatedAt) {
}
// spotless:on
