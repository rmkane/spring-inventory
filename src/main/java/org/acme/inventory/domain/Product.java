package org.acme.inventory.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record Product(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        int quantityOnHand,
        int quantityReserved,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
