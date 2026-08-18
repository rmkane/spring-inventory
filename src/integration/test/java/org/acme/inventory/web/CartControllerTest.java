package org.acme.inventory.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.acme.inventory.dto.cart.CartResponse;
import org.acme.inventory.dto.page.PageResponse;

@Tag("integration")
public class CartControllerTest extends TestSuite {

    @Test
    public void testGetAllCarts() {
        ResponseEntity<PageResponse<CartResponse>> response = restTemplate.exchange(
                "http://localhost:8080/carts",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().content());
        assertFalse(response.getBody().content().isEmpty());

        write(response.getBody(), "get-all-carts.json");
    }
}
