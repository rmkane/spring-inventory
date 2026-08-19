package org.acme.inventory.repository.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Order;
import org.acme.inventory.domain.OrderItem;
import org.acme.inventory.domain.OrderStatus;
import org.acme.inventory.dto.order.OrderItemRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.repository.OrderRepository;
import org.acme.inventory.repository.mapper.order.OrderItemRow;
import org.acme.inventory.repository.mapper.order.OrderItemRowMapper;
import org.acme.inventory.repository.mapper.order.OrderRow;
import org.acme.inventory.repository.mapper.order.OrderRowMapper;
import org.acme.inventory.repository.sql.SqlDateTimes;
import org.acme.inventory.repository.sql.SqlPaging;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private static final String SELECT_ORDERS = """
            SELECT
                id,
                customer_id,
                upper(status::text) AS status,
                created_at,
                updated_at,
                paid_at,
                shipped_at,
                completed_at,
                cancelled_at
            FROM orders
            """;

    private static final String SELECT_ORDER_ITEMS = """
            SELECT
                oi.order_id,
                oi.product_id,
                p.name AS product_name,
                oi.quantity,
                oi.unit_price
            FROM order_items oi
            JOIN products p ON p.id = oi.product_id
            WHERE oi.order_id IN (:orderIds)
            """;

    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("customerId", "(SELECT name FROM customers WHERE id = orders.customer_id)"),
            Map.entry("status", "status"),
            Map.entry("items", "(SELECT count(*) FROM order_items oi WHERE oi.order_id = orders.id)"),
            Map.entry("total", """
                    (SELECT coalesce(sum(oi.quantity * oi.unit_price), 0)
                     FROM order_items oi
                     WHERE oi.order_id = orders.id)
                    """),
            Map.entry("createdAt", "created_at"),
            Map.entry("updatedAt", "updated_at"),
            Map.entry("paidAt", "paid_at"),
            Map.entry("shippedAt", "shipped_at"),
            Map.entry("completedAt", "completed_at"),
            Map.entry("cancelledAt", "cancelled_at"));

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SqlDateTimes sqlDateTimes;
    private final OrderRowMapper orderMapper;
    private final OrderItemRowMapper orderItemMapper;

    @Override
    public List<Order> findAll() {
        return attachItems(jdbcTemplate.query(SELECT_ORDERS, orderMapper));
    }

    @Override
    public PageResult<Order> findPage(PageQuery query) {
        long total = count();
        PageQuery effective = SqlPaging.resolve(query, SORT_COLUMNS, "createdAt", "desc").clampTo(total);
        if (total == 0) {
            return new PageResult<>(List.of(), effective, total);
        }
        List<OrderRow> orders = jdbcTemplate.query(
                SELECT_ORDERS + SqlPaging.orderByLimit(effective, SORT_COLUMNS),
                SqlPaging.params(effective),
                orderMapper);
        return new PageResult<>(attachItems(orders), effective, total);
    }

    @Override
    public long count() {
        return SqlPaging.count(jdbcTemplate, "SELECT count(*) FROM orders");
    }

    @Override
    public Optional<Order> findById(UUID id) {
        List<OrderRow> orders = jdbcTemplate.query(
                SELECT_ORDERS + " WHERE id = :id",
                new MapSqlParameterSource("id", id),
                orderMapper);
        return attachItems(orders).stream().findFirst();
    }

    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        List<OrderRow> orders = jdbcTemplate.query(
                SELECT_ORDERS + " WHERE customer_id = :customerId",
                new MapSqlParameterSource("customerId", customerId),
                orderMapper);
        return attachItems(orders);
    }

    @Override
    public Order insert(
            UUID customerId,
            OrderStatus status,
            OffsetDateTime paidAt,
            OffsetDateTime shippedAt,
            OffsetDateTime completedAt,
            OffsetDateTime cancelledAt,
            List<OrderItemRequest> items) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                """
                        INSERT INTO orders (
                            id, customer_id, status, paid_at, shipped_at, completed_at, cancelled_at)
                        VALUES (
                            :id, :customerId, CAST(:status AS order_status),
                            :paidAt, :shippedAt, :completedAt, :cancelledAt)
                        """,
                orderParams(id, customerId, status, paidAt, shippedAt, completedAt, cancelledAt));
        replaceItems(id, items);
        return findById(id).orElseThrow();
    }

    @Override
    public Optional<Order> update(
            UUID id,
            UUID customerId,
            OrderStatus status,
            OffsetDateTime paidAt,
            OffsetDateTime shippedAt,
            OffsetDateTime completedAt,
            OffsetDateTime cancelledAt,
            List<OrderItemRequest> items) {
        int updated = jdbcTemplate.update(
                """
                        UPDATE orders
                        SET customer_id = :customerId,
                            status = CAST(:status AS order_status),
                            paid_at = :paidAt,
                            shipped_at = :shippedAt,
                            completed_at = :completedAt,
                            cancelled_at = :cancelledAt
                        WHERE id = :id
                        """,
                orderParams(id, customerId, status, paidAt, shippedAt, completedAt, cancelledAt));
        if (updated == 0) {
            return Optional.empty();
        }
        replaceItems(id, items);
        return findById(id);
    }

    @Override
    public boolean deleteById(UUID id) {
        return jdbcTemplate.update(
                "DELETE FROM orders WHERE id = :id",
                new MapSqlParameterSource("id", id)) > 0;
    }

    private void replaceItems(UUID orderId, List<OrderItemRequest> items) {
        jdbcTemplate.update(
                "DELETE FROM order_items WHERE order_id = :orderId",
                new MapSqlParameterSource("orderId", orderId));
        if (items.isEmpty()) {
            return;
        }
        SqlParameterSource[] batch = items.stream()
                .map(item -> new MapSqlParameterSource()
                        .addValue("orderId", orderId)
                        .addValue("productId", item.productId())
                        .addValue("quantity", item.quantity())
                        .addValue("unitPrice", item.unitPrice()))
                .toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(
                """
                        INSERT INTO order_items (order_id, product_id, quantity, unit_price)
                        VALUES (:orderId, :productId, :quantity, :unitPrice)
                        """,
                batch);
    }

    private List<Order> attachItems(List<OrderRow> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }

        List<UUID> orderIds = orders.stream().map(OrderRow::id).toList();
        Map<UUID, List<OrderItem>> itemsByOrderId = jdbcTemplate.query(
                SELECT_ORDER_ITEMS,
                new MapSqlParameterSource("orderIds", orderIds),
                orderItemMapper)
                .stream()
                .collect(Collectors.groupingBy(
                        OrderItemRow::orderId,
                        Collectors.mapping(OrderItemRow::toOrderItem, Collectors.toList())));

        return orders.stream()
                .map(order -> order.toOrder(itemsByOrderId.getOrDefault(order.id(), List.of())))
                .toList();
    }

    private MapSqlParameterSource orderParams(
            UUID id,
            UUID customerId,
            OrderStatus status,
            OffsetDateTime paidAt,
            OffsetDateTime shippedAt,
            OffsetDateTime completedAt,
            OffsetDateTime cancelledAt) {
        return new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("customerId", customerId)
                .addValue("status", status.name().toLowerCase(Locale.ROOT))
                .addValue("paidAt", sqlDateTimes.bind(paidAt))
                .addValue("shippedAt", sqlDateTimes.bind(shippedAt))
                .addValue("completedAt", sqlDateTimes.bind(completedAt))
                .addValue("cancelledAt", sqlDateTimes.bind(cancelledAt));
    }
}
