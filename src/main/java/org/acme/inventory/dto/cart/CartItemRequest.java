package org.acme.inventory.dto.cart;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A product and quantity in a cart")
public record CartItemRequest(
        @NotNull @Schema(description = "Product id") UUID productId,
        @Min(1) @Schema(description = "Quantity to add", example = "2") int quantity) {
}
