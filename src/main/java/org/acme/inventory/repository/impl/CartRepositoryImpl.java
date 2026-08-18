package org.acme.inventory.repository.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Cart;
import org.acme.inventory.domain.CartItem;
import org.acme.inventory.dto.cart.CartItemRequest;
import org.acme.inventory.repository.CartRepository;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private static final RowMapper<CartRow> CART_MAPPER = DataClassRowMapper.newInstance(CartRow.class);
    private static final RowMapper<CartItemRow> CART_ITEM_MAPPER = DataClassRowMapper.newInstance(CartItemRow.class);

    private static final String SELECT_CARTS = """
            SELECT id, customer_id, created_at, updated_at
            FROM carts
            """;

    private static final String SELECT_CART_ITEMS = """
            SELECT
                ci.cart_id,
                ci.product_id,
                p.name AS product_name,
                ci.quantity,
                ci.added_at,
                ci.updated_at
            FROM cart_items ci
            JOIN products p ON p.id = ci.product_id
            WHERE ci.cart_id IN (:cartIds)
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Cart> findAll() {
        return attachItems(jdbcTemplate.query(SELECT_CARTS, CART_MAPPER));
    }

    @Override
    public Optional<Cart> findById(UUID id) {
        List<CartRow> carts = jdbcTemplate.query(
                SELECT_CARTS + " WHERE id = :id",
                new MapSqlParameterSource("id", id),
                CART_MAPPER);
        return attachItems(carts).stream().findFirst();
    }

    @Override
    public List<Cart> findByCustomerId(UUID customerId) {
        List<CartRow> carts = jdbcTemplate.query(
                SELECT_CARTS + " WHERE customer_id = :customerId",
                new MapSqlParameterSource("customerId", customerId),
                CART_MAPPER);
        return attachItems(carts);
    }

    @Override
    public Cart insert(UUID customerId, List<CartItemRequest> items) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO carts (id, customer_id) VALUES (:id, :customerId)",
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("customerId", customerId));
        replaceItems(id, items);
        return findById(id).orElseThrow();
    }

    @Override
    public Optional<Cart> update(UUID id, UUID customerId, List<CartItemRequest> items) {
        int updated = jdbcTemplate.update(
                "UPDATE carts SET customer_id = :customerId WHERE id = :id",
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("customerId", customerId));
        if (updated == 0) {
            return Optional.empty();
        }
        replaceItems(id, items);
        return findById(id);
    }

    @Override
    public boolean deleteById(UUID id) {
        return jdbcTemplate.update(
                "DELETE FROM carts WHERE id = :id",
                new MapSqlParameterSource("id", id)) > 0;
    }

    private void replaceItems(UUID cartId, List<CartItemRequest> items) {
        jdbcTemplate.update(
                "DELETE FROM cart_items WHERE cart_id = :cartId",
                new MapSqlParameterSource("cartId", cartId));
        if (items.isEmpty()) {
            return;
        }
        SqlParameterSource[] batch = items.stream()
                .map(item -> new MapSqlParameterSource()
                        .addValue("cartId", cartId)
                        .addValue("productId", item.productId())
                        .addValue("quantity", item.quantity()))
                .toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(
                """
                        INSERT INTO cart_items (cart_id, product_id, quantity)
                        VALUES (:cartId, :productId, :quantity)
                        """,
                batch);
    }

    private List<Cart> attachItems(List<CartRow> carts) {
        if (carts.isEmpty()) {
            return List.of();
        }

        List<UUID> cartIds = carts.stream().map(CartRow::id).toList();
        Map<UUID, List<CartItem>> itemsByCartId = jdbcTemplate.query(
                SELECT_CART_ITEMS,
                new MapSqlParameterSource("cartIds", cartIds),
                CART_ITEM_MAPPER)
                .stream()
                .collect(Collectors.groupingBy(
                        CartItemRow::cartId,
                        Collectors.mapping(CartItemRow::toCartItem, Collectors.toList())));

        return carts.stream()
                .map(cart -> cart.toCart(itemsByCartId.getOrDefault(cart.id(), List.of())))
                .toList();
    }

    private record CartRow(
            UUID id,
            UUID customerId,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {

        private Cart toCart(List<CartItem> items) {
            return new Cart(id, customerId, createdAt, updatedAt, List.copyOf(items));
        }
    }

    private record CartItemRow(
            UUID cartId,
            UUID productId,
            String productName,
            int quantity,
            OffsetDateTime addedAt,
            OffsetDateTime updatedAt) {

        private CartItem toCartItem() {
            return new CartItem(productId, productName, quantity, addedAt, updatedAt);
        }
    }
}
