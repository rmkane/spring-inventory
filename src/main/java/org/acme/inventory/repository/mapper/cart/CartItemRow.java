package org.acme.inventory.repository.mapper.cart;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.acme.inventory.domain.CartItem;

public record CartItemRow(
        UUID cartId,
        UUID productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        OffsetDateTime addedAt,
        OffsetDateTime updatedAt) {

    public CartItem toCartItem() {
        return new CartItem(productId, productName, quantity, unitPrice, addedAt, updatedAt);
    }
}
