package org.acme.inventory.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void lineTotalIsQuantityTimesUnitPrice() {
        OrderItem item = item(3, "12.50");
        assertEquals(new BigDecimal("37.50"), item.lineTotal());
    }

    @Test
    void totalSumsLineTotals() {
        Order order = order(List.of(item(2, "10.00"), item(1, "4.25")));
        assertEquals(new BigDecimal("24.25"), order.total());
    }

    @Test
    void emptyOrderTotalIsZero() {
        assertEquals(BigDecimal.ZERO, order(List.of()).total());
    }

    private static Order order(List<OrderItem> items) {
        OffsetDateTime now = OffsetDateTime.parse("2026-03-15T14:30:00Z");
        return new Order(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OrderStatus.PENDING,
                now,
                now,
                null,
                null,
                null,
                null,
                items);
    }

    private static OrderItem item(int quantity, String unitPrice) {
        return new OrderItem(UUID.randomUUID(), "Widget", quantity, new BigDecimal(unitPrice));
    }
}
