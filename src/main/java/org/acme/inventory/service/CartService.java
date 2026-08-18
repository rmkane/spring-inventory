package org.acme.inventory.service;

import java.util.List;
import java.util.UUID;

import org.acme.inventory.domain.Cart;
import org.acme.inventory.dto.cart.CartRequest;

public interface CartService {

    List<Cart> getCarts();

    Cart getCartById(UUID id);

    List<Cart> getCartsByCustomerId(UUID customerId);

    Cart createCart(CartRequest request);

    Cart updateCart(UUID id, CartRequest request);

    void deleteCart(UUID id);
}
