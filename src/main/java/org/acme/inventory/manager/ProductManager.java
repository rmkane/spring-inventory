package org.acme.inventory.manager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Product;
import org.acme.inventory.dto.product.ProductRequest;

public interface ProductManager {

    List<Product> getProducts();

    Optional<Product> getProductById(UUID id);

    Product createProduct(ProductRequest request);

    Optional<Product> updateProduct(UUID id, ProductRequest request);

    boolean deleteProduct(UUID id);
}
