package org.acme.inventory.dto.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class InventoryRequestTest {

    @Test
    void createAllowsReservedEqualToOnHand() {
        UUID productId = UUID.randomUUID();
        InventoryCreateRequest request = new InventoryCreateRequest(productId, 10, 10);
        assertEquals(productId, request.productId());
        assertEquals(10, request.quantityOnHand());
        assertEquals(10, request.quantityReserved());
    }

    @Test
    void createRejectsReservedAboveOnHand() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryCreateRequest(UUID.randomUUID(), 5, 6));
    }

    @Test
    void updateRejectsReservedAboveOnHand() {
        assertThrows(IllegalArgumentException.class, () -> new InventoryUpdateRequest(8, 9));
    }
}
