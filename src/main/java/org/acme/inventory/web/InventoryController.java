package org.acme.inventory.web;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.dto.inventory.InventoryCreateRequest;
import org.acme.inventory.dto.inventory.InventoryResponse;
import org.acme.inventory.dto.inventory.InventoryUpdateRequest;
import org.acme.inventory.mapper.InventoryMapper;
import org.acme.inventory.service.InventoryService;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock on hand and reserved quantities")
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    @GetMapping
    @Operation(summary = "List inventory")
    @ApiResponse(responseCode = "200", description = "Inventory returned")
    public ResponseEntity<List<InventoryResponse>> getInventory() {
        return ResponseEntity.ok(inventoryMapper.toResponses(inventoryService.getInventory()));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get inventory for a product")
    @ApiResponse(responseCode = "200", description = "Inventory found")
    @ApiResponse(responseCode = "404", description = "Inventory not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<InventoryResponse> getInventoryByProductId(
            @Parameter(description = "Product id") @PathVariable UUID productId) {
        return ResponseEntity.ok(inventoryMapper.toResponse(inventoryService.getInventoryByProductId(productId)));
    }

    @PostMapping
    @Operation(summary = "Create inventory", description = "Creates a stock row for a product that does not already have one.")
    @ApiResponse(responseCode = "201", description = "Inventory created")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Inventory already exists or product is missing", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody InventoryCreateRequest request) {
        InventoryResponse body = inventoryMapper.toResponse(inventoryService.createInventory(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{productId}")
                .buildAndExpand(body.productId())
                .toUri();
        return ResponseEntity.created(location).body(body);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update inventory quantities")
    @ApiResponse(responseCode = "200", description = "Inventory updated")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Inventory not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<InventoryResponse> updateInventory(
            @Parameter(description = "Product id") @PathVariable UUID productId,
            @Valid @RequestBody InventoryUpdateRequest request) {
        return ResponseEntity.ok(inventoryMapper.toResponse(inventoryService.updateInventory(productId, request)));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete inventory")
    @ApiResponse(responseCode = "204", description = "Inventory deleted", content = @Content)
    @ApiResponse(responseCode = "404", description = "Inventory not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deleteInventory(
            @Parameter(description = "Product id") @PathVariable UUID productId) {
        inventoryService.deleteInventory(productId);
        return ResponseEntity.noContent().build();
    }
}
