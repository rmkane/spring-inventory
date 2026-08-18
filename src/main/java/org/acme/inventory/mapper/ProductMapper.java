package org.acme.inventory.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import org.acme.inventory.domain.Product;
import org.acme.inventory.dto.product.ProductResponse;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantityOnHand(),
                product.getQuantityReserved(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }

    public List<ProductResponse> toResponses(List<Product> products) {
        return products.stream().map(this::toResponse).toList();
    }
}
