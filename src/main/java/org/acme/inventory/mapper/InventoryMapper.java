package org.acme.inventory.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import org.acme.inventory.domain.Inventory;
import org.acme.inventory.dto.inventory.InventoryResponse;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getProductId(),
                inventory.getQuantityOnHand(),
                inventory.getQuantityReserved(),
                inventory.getUpdatedAt());
    }

    public List<InventoryResponse> toResponses(List<Inventory> inventory) {
        // spotless:off
        return inventory.stream()
            .map(this::toResponse)
            .toList();
        // spotless:on
    }
}
