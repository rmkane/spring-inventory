package org.acme.inventory.service;

import java.util.List;
import java.util.UUID;

import org.acme.inventory.domain.Cart;
import org.acme.inventory.dto.cart.CartRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;

public interface CartService {

    List<Cart> getCarts();

    PageResult<Cart> getCarts(PageQuery query);

    long count();

    Cart getCartById(UUID id);

    List<Cart> getCartsByCustomerId(UUID customerId);

    Cart createCart(CartRequest request);

    Cart updateCart(UUID id, CartRequest request);

    void deleteCart(UUID id);
}
