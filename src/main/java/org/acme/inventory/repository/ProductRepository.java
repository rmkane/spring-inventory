package org.acme.inventory.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Product;
import org.acme.inventory.dto.product.ProductRequest;

public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(UUID id);

    Product insert(ProductRequest request);

    Optional<Product> update(UUID id, ProductRequest request);

    boolean deleteById(UUID id);
}
