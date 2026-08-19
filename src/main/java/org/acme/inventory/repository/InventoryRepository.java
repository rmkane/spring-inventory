package org.acme.inventory.repository;

import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Inventory;

public interface InventoryRepository extends JdbcRepository<Inventory, UUID> {

    Inventory insert(UUID productId, int quantityOnHand, int quantityReserved);

    Optional<Inventory> update(UUID productId, int quantityOnHand, int quantityReserved);
}
