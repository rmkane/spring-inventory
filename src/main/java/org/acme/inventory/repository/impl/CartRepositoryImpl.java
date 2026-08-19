package org.acme.inventory.repository.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Cart;
import org.acme.inventory.domain.CartItem;
import org.acme.inventory.dto.cart.CartItemRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.repository.CartRepository;
import org.acme.inventory.repository.mapper.cart.CartItemRow;
import org.acme.inventory.repository.mapper.cart.CartItemRowMapper;
import org.acme.inventory.repository.mapper.cart.CartRow;
import org.acme.inventory.repository.mapper.cart.CartRowMapper;
import org.acme.inventory.repository.sql.SqlPaging;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private static final String SELECT_CARTS = """
            SELECT id, customer_id, created_at, updated_at
            FROM carts
            """;

    private static final String SELECT_CART_ITEMS = """
            SELECT
                ci.cart_id,
                ci.product_id,
                p.name AS product_name,
                p.price AS unit_price,
                ci.quantity,
                ci.added_at,
                ci.updated_at
            FROM cart_items ci
            JOIN products p ON p.id = ci.product_id
            WHERE ci.cart_id IN (:cartIds)
            """;

    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "id", "id",
            "customerId", "(SELECT name FROM customers WHERE id = carts.customer_id)",
            "items", "(SELECT count(*) FROM cart_items ci WHERE ci.cart_id = carts.id)",
            "total", """
                    (SELECT coalesce(sum(ci.quantity * p.price), 0)
                     FROM cart_items ci
                     JOIN products p ON p.id = ci.product_id
                     WHERE ci.cart_id = carts.id)
                    """,
            "createdAt", "created_at",
            "updatedAt", "updated_at");

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final CartRowMapper cartMapper;
    private final CartItemRowMapper cartItemMapper;

    @Override
    public List<Cart> findAll() {
        return attachItems(jdbcTemplate.query(SELECT_CARTS, cartMapper));
    }

    @Override
    public PageResult<Cart> findPage(PageQuery query) {
        long total = count();
        PageQuery effective = SqlPaging.resolve(query, SORT_COLUMNS, "updatedAt", "desc").clampTo(total);
        if (total == 0) {
            return new PageResult<>(List.of(), effective, total);
        }
        List<CartRow> carts = jdbcTemplate.query(
                SELECT_CARTS + SqlPaging.orderByLimit(effective, SORT_COLUMNS),
                SqlPaging.params(effective),
                cartMapper);
        return new PageResult<>(attachItems(carts), effective, total);
    }

    @Override
    public long count() {
        return SqlPaging.count(jdbcTemplate, "SELECT count(*) FROM carts");
    }

    @Override
    public Optional<Cart> findById(UUID id) {
        List<CartRow> carts = jdbcTemplate.query(
                SELECT_CARTS + " WHERE id = :id",
                new MapSqlParameterSource("id", id),
                cartMapper);
        return attachItems(carts).stream().findFirst();
    }

    @Override
    public List<Cart> findByCustomerId(UUID customerId) {
        List<CartRow> carts = jdbcTemplate.query(
                SELECT_CARTS + " WHERE customer_id = :customerId",
                new MapSqlParameterSource("customerId", customerId),
                cartMapper);
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
                cartItemMapper)
                .stream()
                .collect(Collectors.groupingBy(
                        CartItemRow::cartId,
                        Collectors.mapping(CartItemRow::toCartItem, Collectors.toList())));

        return carts.stream()
                .map(cart -> cart.toCart(itemsByCartId.getOrDefault(cart.id(), List.of())))
                .toList();
    }
}
