package org.acme.inventory.web.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.acme.inventory.dto.inventory.InventoryResponse;
import org.acme.inventory.dto.page.PageResponse;

@Tag("integration")
@Tag("legacy")
public class InventoryControllerTest extends TestSuite {

    @Test
    public void testGetAllInventory() {
        ResponseEntity<PageResponse<InventoryResponse>> response = get(
                "/inventory",
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().content().isEmpty());
        write(response.getBody(), "get-all-inventory.json");
    }
}
