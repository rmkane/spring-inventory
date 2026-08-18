package org.acme.inventory.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Inventory(
        UUID productId,
        int quantityOnHand,
        int quantityReserved,
        OffsetDateTime updatedAt) {
}
