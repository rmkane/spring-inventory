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
                cart.getId(),
                cart.getCustomerId(),
                cart.total(),
                cart.getCreatedAt(),
                cart.getUpdatedAt(),
                cart.getItems().stream().map(this::toItemResponse).toList());
    }

    public List<CartResponse> toResponses(List<Cart> carts) {
        // spotless:off
        return carts.stream()
            .map(this::toResponse)
            .toList();
        // spotless:on
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return new CartItemResponse(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.lineTotal(),
                item.getAddedAt(),
                item.getUpdatedAt());
    }
}
