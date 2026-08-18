package org.acme.inventory.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Product;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.dto.product.ProductRequest;
import org.acme.inventory.exception.ResourceNotFoundException;
import org.acme.inventory.manager.ProductManager;
import org.acme.inventory.service.ProductService;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductManager productManager;

    @Override
    public List<Product> getProducts() {
        return productManager.getProducts();
    }

    @Override
    public PageResult<Product> getProducts(PageQuery query) {
        return productManager.getProducts(query);
    }

    @Override
    public long count() {
        return productManager.count();
    }

    @Override
    public Product getProductById(UUID id) {
        return ResourceNotFoundException.require(productManager.getProductById(id), "Product", id);
    }

    @Override
    public Product createProduct(ProductRequest request) {
        return productManager.createProduct(request);
    }

    @Override
    public Product updateProduct(UUID id, ProductRequest request) {
        return ResourceNotFoundException.require(productManager.updateProduct(id, request), "Product", id);
    }

    @Override
    public void deleteProduct(UUID id) {
        ResourceNotFoundException.requireDeleted(productManager.deleteProduct(id), "Product", id);
    }
}
