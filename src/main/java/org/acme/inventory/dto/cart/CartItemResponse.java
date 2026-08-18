package org.acme.inventory.dto.cart;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CartItemResponse(
        UUID productId,
        String productName,
        int quantity,
        OffsetDateTime addedAt,
        OffsetDateTime updatedAt) {
}
