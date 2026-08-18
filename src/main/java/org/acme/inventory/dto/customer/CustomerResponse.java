package org.acme.inventory.dto.customer;

import java.time.OffsetDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

// spotless:off
@Schema(description = "A customer")
public record CustomerResponse(
        @Schema(description = "Customer id", example = "0c1e4a2a-8b3f-4d9e-9a1c-2f7b6d5e4c3b")
        UUID id,

        @Schema(description = "Customer name", example = "Ada Lovelace")
        String name,

        @Schema(description = "Unique email address", example = "ada@example.com")
        String email,

        @Schema(description = "When the customer was created", example = "2026-03-15T14:30:00Z")
        OffsetDateTime createdAt,

        @Schema(description = "When the customer was last updated", example = "2026-03-16T09:12:00Z")
        OffsetDateTime updatedAt) {
}
// spotless:on
