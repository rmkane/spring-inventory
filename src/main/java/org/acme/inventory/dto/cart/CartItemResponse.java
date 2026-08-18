package org.acme.inventory.dto.cart;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CartItemResponse(
        UUID productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        OffsetDateTime addedAt,
        OffsetDateTime updatedAt) {
}
