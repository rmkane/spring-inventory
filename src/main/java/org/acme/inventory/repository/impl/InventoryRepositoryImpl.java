package org.acme.inventory.repository.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Inventory;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.repository.InventoryRepository;
import org.acme.inventory.repository.SqlPaging;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {

    private static final RowMapper<Inventory> INVENTORY_MAPPER = DataClassRowMapper.newInstance(Inventory.class);

    private static final String SELECT_INVENTORY = """
            SELECT product_id, quantity_on_hand, quantity_reserved, updated_at
            FROM inventory
            """;

    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "productId", "(SELECT name FROM products WHERE id = inventory.product_id)",
            "quantityOnHand", "quantity_on_hand",
            "quantityReserved", "quantity_reserved",
            "updatedAt", "updated_at");

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Inventory> findAll() {
        return jdbcTemplate.query(SELECT_INVENTORY, INVENTORY_MAPPER);
    }

    @Override
    public PageResult<Inventory> findPage(PageQuery query) {
        return SqlPaging.fetch(
                jdbcTemplate,
                "SELECT count(*) FROM inventory",
                SELECT_INVENTORY,
                query,
                SORT_COLUMNS,
                "updatedAt",
                "desc",
                INVENTORY_MAPPER);
    }

    @Override
    public long count() {
        return SqlPaging.count(jdbcTemplate, "SELECT count(*) FROM inventory");
    }

    @Override
    public Optional<Inventory> findByProductId(UUID productId) {
        List<Inventory> inventory = jdbcTemplate.query(
                SELECT_INVENTORY + " WHERE product_id = :productId",
                new MapSqlParameterSource("productId", productId),
                INVENTORY_MAPPER);
        return inventory.stream().findFirst();
    }

    @Override
    public Inventory insert(UUID productId, int quantityOnHand, int quantityReserved) {
        jdbcTemplate.update(
                """
                        INSERT INTO inventory (product_id, quantity_on_hand, quantity_reserved)
                        VALUES (:productId, :quantityOnHand, :quantityReserved)
                        """,
                inventoryParams(productId, quantityOnHand, quantityReserved));
        return findByProductId(productId).orElseThrow();
    }

    @Override
    public Optional<Inventory> update(UUID productId, int quantityOnHand, int quantityReserved) {
        int updated = jdbcTemplate.update(
                """
                        UPDATE inventory
                        SET quantity_on_hand = :quantityOnHand, quantity_reserved = :quantityReserved
                        WHERE product_id = :productId
                        """,
                inventoryParams(productId, quantityOnHand, quantityReserved));
        if (updated == 0) {
            return Optional.empty();
        }
        return findByProductId(productId);
    }

    @Override
    public boolean deleteByProductId(UUID productId) {
        return jdbcTemplate.update(
                "DELETE FROM inventory WHERE product_id = :productId",
                new MapSqlParameterSource("productId", productId)) > 0;
    }

    private static MapSqlParameterSource inventoryParams(UUID productId, int quantityOnHand, int quantityReserved) {
        return new MapSqlParameterSource()
                .addValue("productId", productId)
                .addValue("quantityOnHand", quantityOnHand)
                .addValue("quantityReserved", quantityReserved);
    }
}
