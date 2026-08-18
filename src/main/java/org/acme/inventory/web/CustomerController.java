package org.acme.inventory.web;

import java.net.URI;
import java.util.List;
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

import org.acme.inventory.dto.cart.CartResponse;
import org.acme.inventory.dto.customer.CustomerRequest;
import org.acme.inventory.dto.customer.CustomerResponse;
import org.acme.inventory.dto.order.OrderResponse;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResponse;
import org.acme.inventory.mapper.CartMapper;
import org.acme.inventory.mapper.CustomerMapper;
import org.acme.inventory.mapper.OrderMapper;
import org.acme.inventory.service.CartService;
import org.acme.inventory.service.CustomerService;
import org.acme.inventory.service.OrderService;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer accounts")
public class CustomerController {

    private final CustomerService customerService;
    private final CartService cartService;
    private final OrderService orderService;
    private final CustomerMapper customerMapper;
    private final CartMapper cartMapper;
    private final OrderMapper orderMapper;

    @GetMapping
    @Operation(summary = "List customers")
    @ApiResponse(responseCode = "200", description = "Customers returned")
    public ResponseEntity<PageResponse<CustomerResponse>> getCustomers(@ParameterObject PageQuery pageQuery) {
        return ResponseEntity
                .ok(PageResponse.from(customerService.getCustomers(pageQuery), customerMapper::toResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a customer")
    @ApiResponse(responseCode = "200", description = "Customer found")
    @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<CustomerResponse> getCustomerById(
            @Parameter(description = "Customer id") @PathVariable UUID id) {
        return ResponseEntity.ok(customerMapper.toResponse(customerService.getCustomerById(id)));
    }

    @GetMapping("/{id}/carts")
    @Operation(summary = "List a customer's carts")
    @ApiResponse(responseCode = "200", description = "Carts returned")
    @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<List<CartResponse>> getCustomerCarts(
            @Parameter(description = "Customer id") @PathVariable UUID id) {
        customerService.getCustomerById(id);
        return ResponseEntity.ok(cartMapper.toResponses(cartService.getCartsByCustomerId(id)));
    }

    @GetMapping("/{id}/orders")
    @Operation(summary = "List a customer's orders")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(
            @Parameter(description = "Customer id") @PathVariable UUID id) {
        customerService.getCustomerById(id);
        return ResponseEntity.ok(orderMapper.toResponses(orderService.getOrdersByCustomerId(id)));
    }

    @PostMapping
    @Operation(summary = "Create a customer")
    @ApiResponse(responseCode = "201", description = "Customer created")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse body = customerMapper.toResponse(customerService.createCustomer(request));
        return ResponseEntity.created(location(body.id())).body(body);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a customer")
    @ApiResponse(responseCode = "200", description = "Customer updated")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<CustomerResponse> updateCustomer(
            @Parameter(description = "Customer id") @PathVariable UUID id,
            @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerMapper.toResponse(customerService.updateCustomer(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer", description = "Also deletes the customer's carts. Fails if the customer has orders.")
    @ApiResponse(responseCode = "204", description = "Customer deleted", content = @Content)
    @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Customer is still referenced", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer id") @PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    private static URI location(UUID id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
