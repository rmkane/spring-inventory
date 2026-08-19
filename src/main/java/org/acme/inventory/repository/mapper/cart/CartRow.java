package org.acme.inventory.repository.mapper.cart;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.acme.inventory.domain.Cart;
import org.acme.inventory.domain.CartItem;

public record CartRow(
        UUID id,
        UUID customerId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {

    public Cart toCart(List<CartItem> items) {
        return new Cart(id, customerId, createdAt, updatedAt, List.copyOf(items));
    }
}
