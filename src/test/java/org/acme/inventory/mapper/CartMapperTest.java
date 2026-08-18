package org.acme.inventory.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import org.acme.inventory.domain.Cart;
import org.acme.inventory.domain.CartItem;
import org.acme.inventory.dto.cart.CartResponse;

class CartMapperTest {

    private final CartMapper mapper = new CartMapper();

    @Test
    void toResponseIncludesTotals() {
        OffsetDateTime now = OffsetDateTime.parse("2026-03-15T14:30:00Z");
        UUID productId = UUID.fromString("7c9e6679-7425-40de-944b-e07fc1f90ae7");
        CartItem item = new CartItem(productId, "Mouse", 2, new BigDecimal("29.99"), now, now);
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), now, now, List.of(item));

        CartResponse response = mapper.toResponse(cart);

        assertEquals(cart.getId(), response.id());
        assertEquals(new BigDecimal("59.98"), response.total());
        assertEquals(1, response.items().size());
        assertEquals(productId, response.items().get(0).productId());
        assertEquals(new BigDecimal("59.98"), response.items().get(0).lineTotal());
    }
}
