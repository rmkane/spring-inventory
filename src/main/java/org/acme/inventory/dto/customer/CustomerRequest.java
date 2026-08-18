package org.acme.inventory.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for creating or updating a customer")
public record CustomerRequest(
        @NotBlank @Schema(description = "Customer name", example = "Ada Lovelace") String name,
        @NotBlank @Email @Schema(description = "Unique email address", example = "ada@example.com") String email) {
}
