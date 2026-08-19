package org.acme.inventory.repository;

import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Product;
import org.acme.inventory.dto.product.ProductRequest;

public interface ProductRepository extends JdbcRepository<Product, UUID> {

    Product insert(ProductRequest request);

    Optional<Product> update(UUID id, ProductRequest request);
}
