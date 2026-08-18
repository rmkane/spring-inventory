package org.acme.inventory.web.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import org.acme.inventory.dto.cart.CartItemRequest;
import org.acme.inventory.dto.cart.CartRequest;
import org.acme.inventory.dto.cart.CartResponse;
import org.acme.inventory.dto.customer.CustomerRequest;
import org.acme.inventory.dto.customer.CustomerResponse;
import org.acme.inventory.dto.page.PageResponse;
import org.acme.inventory.dto.product.ProductRequest;
import org.acme.inventory.dto.product.ProductResponse;

@Tag("integration")
@Tag("legacy")
public class CartControllerTest extends TestSuite {

    @Test
    public void testGetAllCarts() {
        ResponseEntity<PageResponse<CartResponse>> response = get(
                "/carts",
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().content());
        assertFalse(response.getBody().content().isEmpty());

        write(response.getBody(), "get-all-carts.json");
    }

    @Test
    public void testCartCrud() {
        CustomerResponse customer = post(
                "/customers",
                new CustomerRequest("IT Cart Customer", "it-cart-" + UUID.randomUUID() + "@example.com"),
                CustomerResponse.class)
                .getBody();
        ProductResponse product = post(
                "/products",
                new ProductRequest("IT Cart Product", null, new BigDecimal("10.00")),
                ProductResponse.class)
                .getBody();

        CartRequest createRequest = new CartRequest(
                customer.id(),
                List.of(new CartItemRequest(product.id(), 2)));
        ResponseEntity<CartResponse> created = post("/carts", createRequest, CartResponse.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        CartResponse cart = created.getBody();
        assertNotNull(cart);
        assertEquals(customer.id(), cart.customerId());
        assertEquals(1, cart.items().size());
        assertEquals(2, cart.items().get(0).quantity());
        assertEquals(0, cart.total().compareTo(new BigDecimal("20.00")));
        write(cart, "create-cart.json");

        ResponseEntity<CartResponse> fetched = get("/carts/" + cart.id(), CartResponse.class);
        assertEquals(HttpStatus.OK, fetched.getStatusCode());
        assertEquals(cart.id(), fetched.getBody().id());

        CartRequest updateRequest = new CartRequest(
                customer.id(),
                List.of(new CartItemRequest(product.id(), 3)));
        ResponseEntity<CartResponse> updated = put("/carts/" + cart.id(), updateRequest, CartResponse.class);
        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertEquals(3, updated.getBody().items().get(0).quantity());
        assertEquals(0, updated.getBody().total().compareTo(new BigDecimal("30.00")));
        write(updated.getBody(), "update-cart.json");

        ResponseEntity<Void> deleted = delete("/carts/" + cart.id());
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());

        ResponseEntity<ProblemDetail> missing = get("/carts/" + cart.id(), ProblemDetail.class);
        assertEquals(HttpStatus.NOT_FOUND, missing.getStatusCode());

        delete("/products/" + product.id());
        delete("/customers/" + customer.id());
    }
}
