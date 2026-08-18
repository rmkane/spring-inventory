package org.acme.inventory.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import org.acme.inventory.dto.inventory.InventoryResponse;
import org.acme.inventory.dto.inventory.InventoryUpdateRequest;
import org.acme.inventory.dto.page.PageResponse;
import org.acme.inventory.dto.product.ProductRequest;
import org.acme.inventory.dto.product.ProductResponse;

@Tag("integration")
public class ProductControllerTest extends TestSuite {

    @Test
    public void testGetAllProducts() {
        ResponseEntity<PageResponse<ProductResponse>> response = get(
                "/products",
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().content());
        assertFalse(response.getBody().content().isEmpty());

        write(response.getBody(), "get-all-products.json");
    }

    @Test
    public void testProductCrud() {
        ProductRequest createRequest = new ProductRequest("IT Mouse", "Integration test mouse",
                new BigDecimal("19.99"));
        ResponseEntity<ProductResponse> created = post("/products", createRequest, ProductResponse.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        ProductResponse product = created.getBody();
        assertNotNull(product);
        assertNotNull(product.id());
        assertEquals("IT Mouse", product.name());
        assertEquals(0, product.price().compareTo(new BigDecimal("19.99")));
        assertEquals(0, product.quantityOnHand());
        write(product, "create-product.json");

        ResponseEntity<ProductResponse> fetched = get("/products/" + product.id(), ProductResponse.class);
        assertEquals(HttpStatus.OK, fetched.getStatusCode());
        assertEquals(product.id(), fetched.getBody().id());
        assertEquals("IT Mouse", fetched.getBody().name());
        write(fetched.getBody(), "get-product.json");

        ProductRequest updateRequest = new ProductRequest("IT Mouse Pro", "Updated mouse", new BigDecimal("24.50"));
        ResponseEntity<ProductResponse> updated = put("/products/" + product.id(), updateRequest,
                ProductResponse.class);
        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertEquals("IT Mouse Pro", updated.getBody().name());
        assertEquals(0, updated.getBody().price().compareTo(new BigDecimal("24.50")));
        write(updated.getBody(), "update-product.json");

        ResponseEntity<InventoryResponse> inventory = get("/inventory/" + product.id(), InventoryResponse.class);
        assertEquals(HttpStatus.OK, inventory.getStatusCode());
        assertEquals(product.id(), inventory.getBody().productId());
        assertEquals(0, inventory.getBody().quantityOnHand());

        ResponseEntity<InventoryResponse> stocked = put(
                "/inventory/" + product.id(),
                new InventoryUpdateRequest(25, 3),
                InventoryResponse.class);
        assertEquals(HttpStatus.OK, stocked.getStatusCode());
        assertEquals(25, stocked.getBody().quantityOnHand());
        assertEquals(3, stocked.getBody().quantityReserved());
        write(stocked.getBody(), "update-inventory.json");

        ResponseEntity<Void> deleted = delete("/products/" + product.id());
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());

        ResponseEntity<ProblemDetail> missing = get("/products/" + product.id(), ProblemDetail.class);
        assertEquals(HttpStatus.NOT_FOUND, missing.getStatusCode());
        assertTrue(missing.getBody().getDetail().contains(product.id().toString()));
        write(missing.getBody(), "missing-product.json");
    }

    @Test
    public void testGetMissingProduct() {
        UUID missingId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        ResponseEntity<ProblemDetail> response = get("/products/" + missingId, ProblemDetail.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody().getTitle());
    }
}
