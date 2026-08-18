package org.acme.inventory.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CartItem(
        UUID productId,
        String productName,
        int quantity,
        OffsetDateTime addedAt,
        OffsetDateTime updatedAt) {
}
