package org.acme.inventory.dto.order;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A product line on an order. Unit price is snapshotted from the catalog when omitted.")
public record OrderItemRequest(
        @NotNull @Schema(description = "Product id") UUID productId,
        @Min(1) @Schema(description = "Quantity ordered", example = "1") int quantity,
        @DecimalMin("0.00") @Schema(description = "Optional unit price override", example = "29.99") BigDecimal unitPrice) {
}
