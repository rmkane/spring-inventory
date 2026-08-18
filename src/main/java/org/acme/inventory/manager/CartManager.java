package org.acme.inventory.manager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Cart;
import org.acme.inventory.dto.cart.CartRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;

public interface CartManager {

    List<Cart> getCarts();

    PageResult<Cart> getCarts(PageQuery query);

    long count();

    Optional<Cart> getCartById(UUID id);

    List<Cart> getCartsByCustomerId(UUID customerId);

    Cart createCart(CartRequest request);

    Optional<Cart> updateCart(UUID id, CartRequest request);

    boolean deleteCart(UUID id);
}
