package org.acme.inventory.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record Cart(
        UUID id,
        UUID customerId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<CartItem> items) {

    public BigDecimal total() {
        return items.stream().map(CartItem::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
