package org.acme.inventory.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ResourceNotFoundExceptionTest {

    @Test
    void requireReturnsTheValue() {
        assertEquals("cart", ResourceNotFoundException.require(Optional.of("cart"), "Cart", 1));
    }

    @Test
    void requireThrowsWhenEmpty() {
        UUID id = UUID.fromString("7c9e6679-7425-40de-944b-e07fc1f90ae7");
        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> ResourceNotFoundException.require(Optional.empty(), "Product", id));
        assertEquals("Product not found: " + id, ex.getMessage());
    }

    @Test
    void requireDeletedThrowsWhenMissing() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> ResourceNotFoundException.requireDeleted(false, "Order", 9));
        ResourceNotFoundException.requireDeleted(true, "Order", 9);
    }
}
