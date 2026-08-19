package org.acme.inventory.repository.mapper.order;

import java.math.BigDecimal;
import java.util.UUID;

import org.acme.inventory.domain.OrderItem;

public record OrderItemRow(
        UUID orderId,
        UUID productId,
        String productName,
        int quantity,
        BigDecimal unitPrice) {

    public OrderItem toOrderItem() {
        return new OrderItem(productId, productName, quantity, unitPrice);
    }
}
