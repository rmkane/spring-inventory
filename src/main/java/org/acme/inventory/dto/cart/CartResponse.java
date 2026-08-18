package org.acme.inventory.dto.cart;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID id,
        UUID customerId,
        BigDecimal total,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<CartItemResponse> items) {
}
