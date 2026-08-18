package org.acme.inventory.dto.inventory;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InventoryResponse(
        UUID productId,
        int quantityOnHand,
        int quantityReserved,
        OffsetDateTime updatedAt) {
}
