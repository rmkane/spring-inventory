package org.acme.inventory.service;

import java.util.List;
import java.util.UUID;

import org.acme.inventory.domain.Product;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.dto.product.ProductRequest;

public interface ProductService {

    List<Product> getProducts();

    PageResult<Product> getProducts(PageQuery query);

    long count();

    Product getProductById(UUID id);

    Product createProduct(ProductRequest request);

    Product updateProduct(UUID id, ProductRequest request);

    void deleteProduct(UUID id);
}
