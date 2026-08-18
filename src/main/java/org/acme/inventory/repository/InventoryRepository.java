package org.acme.inventory.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Inventory;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;

public interface InventoryRepository {

    List<Inventory> findAll();

    PageResult<Inventory> findPage(PageQuery query);

    long count();

    Optional<Inventory> findByProductId(UUID productId);

    Inventory insert(UUID productId, int quantityOnHand, int quantityReserved);

    Optional<Inventory> update(UUID productId, int quantityOnHand, int quantityReserved);

    boolean deleteByProductId(UUID productId);
}
