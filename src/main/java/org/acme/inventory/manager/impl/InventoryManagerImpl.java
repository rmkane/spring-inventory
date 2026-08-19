package org.acme.inventory.manager.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Inventory;
import org.acme.inventory.dto.inventory.InventoryCreateRequest;
import org.acme.inventory.dto.inventory.InventoryUpdateRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.manager.InventoryManager;
import org.acme.inventory.repository.InventoryRepository;

@Service
@RequiredArgsConstructor
public class InventoryManagerImpl implements InventoryManager {

    private final InventoryRepository inventoryRepository;

    @Override
    public List<Inventory> getInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public PageResult<Inventory> getInventory(PageQuery query) {
        return inventoryRepository.findPage(query);
    }

    @Override
    public long count() {
        return inventoryRepository.count();
    }

    @Override
    public Optional<Inventory> getInventoryByProductId(UUID productId) {
        return inventoryRepository.findById(productId);
    }

    @Override
    @Transactional
    public Inventory createInventory(InventoryCreateRequest request) {
        return inventoryRepository.insert(request.productId(), request.quantityOnHand(), request.quantityReserved());
    }

    @Override
    @Transactional
    public Optional<Inventory> updateInventory(UUID productId, InventoryUpdateRequest request) {
        return inventoryRepository.update(productId, request.quantityOnHand(), request.quantityReserved());
    }

    @Override
    @Transactional
    public boolean deleteInventory(UUID productId) {
        return inventoryRepository.deleteById(productId);
    }
}
