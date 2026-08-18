package org.acme.inventory.dto.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for creating or updating a product")
public record ProductRequest(
        @NotBlank @Schema(description = "Product name", example = "Wireless mouse") String name,
        @Schema(description = "Optional product description", example = "Ergonomic wireless mouse") String description,
        @NotNull @DecimalMin("0.00") @Schema(description = "Unit price", example = "29.99") BigDecimal price) {
}
