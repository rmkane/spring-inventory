package org.acme.inventory.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Product;
import org.acme.inventory.dto.product.ProductRequest;
import org.acme.inventory.repository.ProductRepository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private static final RowMapper<Product> PRODUCT_MAPPER = DataClassRowMapper.newInstance(Product.class);

    private static final String SELECT_PRODUCTS = """
            SELECT
                p.id,
                p.name,
                p.description,
                p.price,
                COALESCE(i.quantity_on_hand, 0) AS quantity_on_hand,
                COALESCE(i.quantity_reserved, 0) AS quantity_reserved,
                p.created_at,
                p.updated_at
            FROM products p
            LEFT JOIN inventory i ON i.product_id = p.id
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Product> findAll() {
        return jdbcTemplate.query(SELECT_PRODUCTS, PRODUCT_MAPPER);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        List<Product> products = jdbcTemplate.query(
                SELECT_PRODUCTS + " WHERE p.id = :id",
                new MapSqlParameterSource("id", id),
                PRODUCT_MAPPER);
        return products.stream().findFirst();
    }

    @Override
    public Product insert(ProductRequest request) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                """
                        INSERT INTO products (id, name, description, price)
                        VALUES (:id, :name, :description, :price)
                        """,
                productParams(id, request));
        return findById(id).orElseThrow();
    }

    @Override
    public Optional<Product> update(UUID id, ProductRequest request) {
        int updated = jdbcTemplate.update(
                """
                        UPDATE products
                        SET name = :name, description = :description, price = :price
                        WHERE id = :id
                        """,
                productParams(id, request));
        if (updated == 0) {
            return Optional.empty();
        }
        return findById(id);
    }

    @Override
    public boolean deleteById(UUID id) {
        return jdbcTemplate.update(
                "DELETE FROM products WHERE id = :id",
                new MapSqlParameterSource("id", id)) > 0;
    }

    private static MapSqlParameterSource productParams(UUID id, ProductRequest request) {
        return new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("name", request.name())
                .addValue("description", request.description())
                .addValue("price", request.price());
    }
}
