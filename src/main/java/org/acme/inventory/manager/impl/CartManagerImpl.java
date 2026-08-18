package org.acme.inventory.manager.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Cart;
import org.acme.inventory.dto.cart.CartItemRequest;
import org.acme.inventory.dto.cart.CartRequest;
import org.acme.inventory.manager.CartManager;
import org.acme.inventory.repository.CartRepository;

@Service
@RequiredArgsConstructor
public class CartManagerImpl implements CartManager {

    private final CartRepository cartRepository;

    @Override
    public List<Cart> getCarts() {
        return cartRepository.findAll();
    }

    @Override
    public Optional<Cart> getCartById(UUID id) {
        return cartRepository.findById(id);
    }

    @Override
    public List<Cart> getCartsByCustomerId(UUID customerId) {
        return cartRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional
    public Cart createCart(CartRequest request) {
        return cartRepository.insert(request.customerId(), mergeItems(request.items()));
    }

    @Override
    @Transactional
    public Optional<Cart> updateCart(UUID id, CartRequest request) {
        return cartRepository.update(id, request.customerId(), mergeItems(request.items()));
    }

    @Override
    @Transactional
    public boolean deleteCart(UUID id) {
        return cartRepository.deleteById(id);
    }

    private static List<CartItemRequest> mergeItems(List<CartItemRequest> items) {
        return items.stream()
                .collect(Collectors.toMap(
                        CartItemRequest::productId,
                        item -> item,
                        (left, right) -> new CartItemRequest(left.productId(), left.quantity() + right.quantity()),
                        LinkedHashMap::new))
                .values()
                .stream()
                .toList();
    }
}
