package org.acme.inventory.manager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Inventory;
import org.acme.inventory.dto.inventory.InventoryCreateRequest;
import org.acme.inventory.dto.inventory.InventoryUpdateRequest;

public interface InventoryManager {

    List<Inventory> getInventory();

    Optional<Inventory> getInventoryByProductId(UUID productId);

    Inventory createInventory(InventoryCreateRequest request);

    Optional<Inventory> updateInventory(UUID productId, InventoryUpdateRequest request);

    boolean deleteInventory(UUID productId);
}
