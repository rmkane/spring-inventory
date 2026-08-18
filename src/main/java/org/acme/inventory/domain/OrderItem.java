package org.acme.inventory.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItem(
        UUID productId,
        String productName,
        int quantity,
        BigDecimal unitPrice) {
}
