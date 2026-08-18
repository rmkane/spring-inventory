package org.acme.inventory.manager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Product;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.dto.product.ProductRequest;

public interface ProductManager {

    List<Product> getProducts();

    PageResult<Product> getProducts(PageQuery query);

    long count();

    Optional<Product> getProductById(UUID id);

    Product createProduct(ProductRequest request);

    Optional<Product> updateProduct(UUID id, ProductRequest request);

    boolean deleteProduct(UUID id);
}
