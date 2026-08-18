package org.acme.inventory.service;

import java.util.List;
import java.util.UUID;

import org.acme.inventory.domain.Inventory;
import org.acme.inventory.dto.inventory.InventoryCreateRequest;
import org.acme.inventory.dto.inventory.InventoryUpdateRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;

public interface InventoryService {

    List<Inventory> getInventory();

    PageResult<Inventory> getInventory(PageQuery query);

    long count();

    Inventory getInventoryByProductId(UUID productId);

    Inventory createInventory(InventoryCreateRequest request);

    Inventory updateInventory(UUID productId, InventoryUpdateRequest request);

    void deleteInventory(UUID productId);
}
