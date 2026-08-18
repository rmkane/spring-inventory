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

import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResponse;
import org.acme.inventory.dto.product.ProductRequest;
import org.acme.inventory.dto.product.ProductResponse;
import org.acme.inventory.mapper.ProductMapper;
import org.acme.inventory.service.ProductService;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    @Operation(summary = "List products", description = "Returns a page of products with current inventory quantities.")
    @ApiResponse(responseCode = "200", description = "Products returned")
    public ResponseEntity<PageResponse<ProductResponse>> getProducts(@ParameterObject PageQuery pageQuery) {
        return ResponseEntity.ok(PageResponse.from(productService.getProducts(pageQuery), productMapper::toResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product id") @PathVariable UUID id) {
        return ResponseEntity.ok(productMapper.toResponse(productService.getProductById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a product", description = "Creates a product and an empty inventory row.")
    @ApiResponse(responseCode = "201", description = "Product created")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse body = productMapper.toResponse(productService.createProduct(request));
        return ResponseEntity.created(location(body.id())).body(body);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    @ApiResponse(responseCode = "200", description = "Product updated")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product id") @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productMapper.toResponse(productService.updateProduct(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Fails if the product is referenced by an order.")
    @ApiResponse(responseCode = "204", description = "Product deleted", content = @Content)
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Product is still referenced", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product id") @PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    private static URI location(UUID id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
