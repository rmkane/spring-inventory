package org.acme.inventory.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class CartTest {

    @Test
    void lineTotalIsQuantityTimesUnitPrice() {
        CartItem item = item(2, "29.99");
        assertEquals(new BigDecimal("59.98"), item.lineTotal());
    }

    @Test
    void totalSumsLineTotals() {
        Cart cart = cart(List.of(item(2, "10.00"), item(1, "5.50")));
        assertEquals(new BigDecimal("25.50"), cart.total());
    }

    @Test
    void emptyCartTotalIsZero() {
        assertEquals(BigDecimal.ZERO, cart(List.of()).total());
    }

    private static Cart cart(List<CartItem> items) {
        OffsetDateTime now = OffsetDateTime.parse("2026-03-15T14:30:00Z");
        return new Cart(UUID.randomUUID(), UUID.randomUUID(), now, now, items);
    }

    private static CartItem item(int quantity, String unitPrice) {
        OffsetDateTime now = OffsetDateTime.parse("2026-03-15T14:30:00Z");
        return new CartItem(
                UUID.randomUUID(),
                "Widget",
                quantity,
                new BigDecimal(unitPrice),
                now,
                now);
    }
}
