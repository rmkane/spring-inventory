package org.acme.inventory.web.api;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
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

import org.acme.inventory.dto.cart.CartRequest;
import org.acme.inventory.dto.cart.CartResponse;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResponse;
import org.acme.inventory.mapper.CartMapper;
import org.acme.inventory.service.CartService;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Tag(name = "Carts", description = "Shopping carts and line items")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    @GetMapping
    @Operation(summary = "List carts")
    @ApiResponse(responseCode = "200", description = "Carts returned")
    public ResponseEntity<PageResponse<CartResponse>> getCarts(@ParameterObject PageQuery pageQuery) {
        return ResponseEntity.ok(PageResponse.from(cartService.getCarts(pageQuery), cartMapper::toResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a cart")
    @ApiResponse(responseCode = "200", description = "Cart found")
    @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<CartResponse> getCartById(
            @Parameter(description = "Cart id") @PathVariable UUID id) {
        return ResponseEntity.ok(cartMapper.toResponse(cartService.getCartById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a cart", description = "Creates a cart for a customer. Duplicate products in the payload are merged.")
    @ApiResponse(responseCode = "201", description = "Cart created")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Customer or product does not exist", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<CartResponse> createCart(@Valid @RequestBody CartRequest request) {
        CartResponse body = cartMapper.toResponse(cartService.createCart(request));
        return ResponseEntity.created(location(body.id())).body(body);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a cart", description = "Replaces the cart owner and line items.")
    @ApiResponse(responseCode = "200", description = "Cart updated")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Customer or product does not exist", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<CartResponse> updateCart(
            @Parameter(description = "Cart id") @PathVariable UUID id,
            @Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartMapper.toResponse(cartService.updateCart(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a cart")
    @ApiResponse(responseCode = "204", description = "Cart deleted", content = @Content)
    @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deleteCart(
            @Parameter(description = "Cart id") @PathVariable UUID id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

    private static URI location(UUID id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
