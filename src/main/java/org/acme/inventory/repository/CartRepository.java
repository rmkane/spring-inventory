package org.acme.inventory.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Cart;
import org.acme.inventory.dto.cart.CartItemRequest;

public interface CartRepository {

    List<Cart> findAll();

    Optional<Cart> findById(UUID id);

    List<Cart> findByCustomerId(UUID customerId);

    Cart insert(UUID customerId, List<CartItemRequest> items);

    Optional<Cart> update(UUID id, UUID customerId, List<CartItemRequest> items);

    boolean deleteById(UUID id);
}
