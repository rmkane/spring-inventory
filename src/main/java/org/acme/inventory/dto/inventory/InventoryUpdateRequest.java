package org.acme.inventory.dto.inventory;

import jakarta.validation.constraints.Min;

import io.swagger.v3.oas.annotations.media.Schema;

// spotless:off
@Schema(description = "Payload for updating inventory quantities")
public record InventoryUpdateRequest(
        @Min(0)
        @Schema(description = "Quantity on hand", example = "80")
        int quantityOnHand,

        @Min(0)
        @Schema(description = "Quantity reserved", example = "10")
        int quantityReserved) {
// spotless:on

    public InventoryUpdateRequest {
        if (quantityReserved > quantityOnHand) {
            throw new IllegalArgumentException("quantityReserved cannot exceed quantityOnHand");
        }
    }
}
