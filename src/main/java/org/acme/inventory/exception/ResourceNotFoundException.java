package org.acme.inventory.exception;

import java.util.Optional;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found: " + id);
    }

    public static <T> T require(Optional<T> value, String resource, Object id) {
        return value.orElseThrow(() -> new ResourceNotFoundException(resource, id));
    }

    public static void requireDeleted(boolean deleted, String resource, Object id) {
        if (!deleted) {
            throw new ResourceNotFoundException(resource, id);
        }
    }
}
