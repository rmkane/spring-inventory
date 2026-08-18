package org.acme.inventory.dto.inventory;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

// spotless:off
@Schema(description = "Payload for creating an inventory row for a product")
public record InventoryCreateRequest(
        @NotNull
        @Schema(description = "Product to stock")
        UUID productId,

        @Min(0)
        @Schema(description = "Quantity on hand", example = "100")
        int quantityOnHand,

        @Min(0)
        @Schema(description = "Quantity reserved", example = "5")
        int quantityReserved) {
// spotless:on

    public InventoryCreateRequest {
        if (quantityReserved > quantityOnHand) {
            throw new IllegalArgumentException("quantityReserved cannot exceed quantityOnHand");
        }
    }
}
