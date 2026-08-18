package org.acme.inventory.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CartItem(
        UUID productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        OffsetDateTime addedAt,
        OffsetDateTime updatedAt) {

    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
