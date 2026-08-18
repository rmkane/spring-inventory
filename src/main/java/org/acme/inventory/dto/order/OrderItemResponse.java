package org.acme.inventory.dto.order;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

// spotless:off
@Schema(description = "A product line on an order, with the unit price at purchase")
public record OrderItemResponse(
        @Schema(description = "Product id", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
        UUID productId,

        @Schema(description = "Product name", example = "Wireless mouse")
        String productName,

        @Schema(description = "Quantity ordered", example = "1")
        int quantity,

        @Schema(description = "Unit price snapshotted onto the order", example = "29.99")
        BigDecimal unitPrice) {
}
// spotless:on
