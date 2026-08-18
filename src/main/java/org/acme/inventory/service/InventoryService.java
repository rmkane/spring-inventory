package org.acme.inventory.service;

import java.util.List;
import java.util.UUID;

import org.acme.inventory.domain.Inventory;
import org.acme.inventory.dto.inventory.InventoryCreateRequest;
import org.acme.inventory.dto.inventory.InventoryUpdateRequest;

public interface InventoryService {

    List<Inventory> getInventory();

    Inventory getInventoryByProductId(UUID productId);

    Inventory createInventory(InventoryCreateRequest request);

    Inventory updateInventory(UUID productId, InventoryUpdateRequest request);

    void deleteInventory(UUID productId);
}
