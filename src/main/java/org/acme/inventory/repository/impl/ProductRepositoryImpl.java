package org.acme.inventory.repository.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Product;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.dto.product.ProductRequest;
import org.acme.inventory.repository.ProductRepository;
import org.acme.inventory.repository.mapper.product.ProductRowMapper;
import org.acme.inventory.repository.sql.SqlPaging;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

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

    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "name", "p.name",
            "price", "p.price",
            "quantityOnHand", "i.quantity_on_hand",
            "quantityReserved", "i.quantity_reserved",
            "createdAt", "p.created_at",
            "updatedAt", "p.updated_at");

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ProductRowMapper productMapper;

    @Override
    public List<Product> findAll() {
        return jdbcTemplate.query(SELECT_PRODUCTS, productMapper);
    }

    @Override
    public PageResult<Product> findPage(PageQuery query) {
        return SqlPaging.fetch(
                jdbcTemplate,
                "SELECT count(*) FROM products",
                SELECT_PRODUCTS,
                query,
                SORT_COLUMNS,
                "name",
                "asc",
                productMapper);
    }

    @Override
    public long count() {
        return SqlPaging.count(jdbcTemplate, "SELECT count(*) FROM products");
    }

    @Override
    public Optional<Product> findById(UUID id) {
        List<Product> products = jdbcTemplate.query(
                SELECT_PRODUCTS + " WHERE p.id = :id",
                new MapSqlParameterSource("id", id),
                productMapper);
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
