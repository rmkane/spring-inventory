package org.acme.inventory.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Cart;
import org.acme.inventory.dto.cart.CartRequest;
import org.acme.inventory.exception.ResourceNotFoundException;
import org.acme.inventory.manager.CartManager;
import org.acme.inventory.service.CartService;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartManager cartManager;

    @Override
    public List<Cart> getCarts() {
        return cartManager.getCarts();
    }

    @Override
    public Cart getCartById(UUID id) {
        return ResourceNotFoundException.require(cartManager.getCartById(id), "Cart", id);
    }

    @Override
    public List<Cart> getCartsByCustomerId(UUID customerId) {
        return cartManager.getCartsByCustomerId(customerId);
    }

    @Override
    public Cart createCart(CartRequest request) {
        return cartManager.createCart(request);
    }

    @Override
    public Cart updateCart(UUID id, CartRequest request) {
        return ResourceNotFoundException.require(cartManager.updateCart(id, request), "Cart", id);
    }

    @Override
    public void deleteCart(UUID id) {
        ResourceNotFoundException.requireDeleted(cartManager.deleteCart(id), "Cart", id);
    }
}
