package org.acme.inventory.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import org.acme.inventory.domain.Cart;
import org.acme.inventory.domain.CartItem;
import org.acme.inventory.dto.cart.CartItemResponse;
import org.acme.inventory.dto.cart.CartResponse;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {
        return new CartResponse(
                cart.id(),
                cart.customerId(),
                cart.total(),
                cart.createdAt(),
                cart.updatedAt(),
                cart.items().stream().map(this::toItemResponse).toList());
    }

    public List<CartResponse> toResponses(List<Cart> carts) {
        return carts.stream().map(this::toResponse).toList();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return new CartItemResponse(
                item.productId(),
                item.productName(),
                item.quantity(),
                item.unitPrice(),
                item.lineTotal(),
                item.addedAt(),
                item.updatedAt());
    }
}
