package org.acme.inventory.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Inventory;
import org.acme.inventory.dto.inventory.InventoryCreateRequest;
import org.acme.inventory.dto.inventory.InventoryUpdateRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.exception.ResourceNotFoundException;
import org.acme.inventory.manager.InventoryManager;
import org.acme.inventory.service.InventoryService;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryManager inventoryManager;

    @Override
    public List<Inventory> getInventory() {
        return inventoryManager.getInventory();
    }

    @Override
    public PageResult<Inventory> getInventory(PageQuery query) {
        return inventoryManager.getInventory(query);
    }

    @Override
    public long count() {
        return inventoryManager.count();
    }

    @Override
    public Inventory getInventoryByProductId(UUID productId) {
        return ResourceNotFoundException.require(
                inventoryManager.getInventoryByProductId(productId), "Inventory", productId);
    }

    @Override
    public Inventory createInventory(InventoryCreateRequest request) {
        return inventoryManager.createInventory(request);
    }

    @Override
    public Inventory updateInventory(UUID productId, InventoryUpdateRequest request) {
        return ResourceNotFoundException.require(
                inventoryManager.updateInventory(productId, request), "Inventory", productId);
    }

    @Override
    public void deleteInventory(UUID productId) {
        ResourceNotFoundException.requireDeleted(
                inventoryManager.deleteInventory(productId), "Inventory", productId);
    }
}
