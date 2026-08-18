package org.acme.inventory.dto.product;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        int quantityOnHand,
        int quantityReserved,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
