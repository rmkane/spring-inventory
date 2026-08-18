package org.acme.inventory.manager.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Product;
import org.acme.inventory.dto.product.ProductRequest;
import org.acme.inventory.manager.ProductManager;
import org.acme.inventory.repository.InventoryRepository;
import org.acme.inventory.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductManagerImpl implements ProductManager {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public Product createProduct(ProductRequest request) {
        Product product = productRepository.insert(request);
        inventoryRepository.insert(product.id(), 0, 0);
        return productRepository.findById(product.id()).orElseThrow();
    }

    @Override
    @Transactional
    public Optional<Product> updateProduct(UUID id, ProductRequest request) {
        return productRepository.update(id, request);
    }

    @Override
    @Transactional
    public boolean deleteProduct(UUID id) {
        return productRepository.deleteById(id);
    }
}
