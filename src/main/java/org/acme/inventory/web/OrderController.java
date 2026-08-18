package org.acme.inventory.web;

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

import org.acme.inventory.dto.order.OrderRequest;
import org.acme.inventory.dto.order.OrderResponse;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResponse;
import org.acme.inventory.mapper.OrderMapper;
import org.acme.inventory.service.OrderService;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Customer orders and status timestamps")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping
    @Operation(summary = "List orders")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    public ResponseEntity<PageResponse<OrderResponse>> getOrders(@ParameterObject PageQuery pageQuery) {
        return ResponseEntity.ok(PageResponse.from(orderService.getOrders(pageQuery), orderMapper::toResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "Order id") @PathVariable UUID id) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.getOrderById(id)));
    }

    @PostMapping
    @Operation(summary = "Create an order", description = "Creates an order. Unit prices default to the current catalog price. Status defaults to PENDING.")
    @ApiResponse(responseCode = "201", description = "Order created")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Customer or product does not exist", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse body = orderMapper.toResponse(orderService.createOrder(request));
        return ResponseEntity.created(location(body.id())).body(body);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an order", description = "Replaces customer, items, and status. First transition into paid, shipped, completed, or cancelled sets that timestamp.")
    @ApiResponse(responseCode = "200", description = "Order updated")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Customer or product does not exist", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<OrderResponse> updateOrder(
            @Parameter(description = "Order id") @PathVariable UUID id,
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.updateOrder(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order")
    @ApiResponse(responseCode = "204", description = "Order deleted", content = @Content)
    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order id") @PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    private static URI location(UUID id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
