package org.acme.inventory.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Order;
import org.acme.inventory.domain.OrderStatus;
import org.acme.inventory.dto.order.OrderItemRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;

public interface OrderRepository {

    List<Order> findAll();

    PageResult<Order> findPage(PageQuery query);

    long count();

    Optional<Order> findById(UUID id);

    List<Order> findByCustomerId(UUID customerId);

    Order insert(
            UUID customerId,
            OrderStatus status,
            OffsetDateTime paidAt,
            OffsetDateTime shippedAt,
            OffsetDateTime completedAt,
            OffsetDateTime cancelledAt,
            List<OrderItemRequest> items);

    Optional<Order> update(
            UUID id,
            UUID customerId,
            OrderStatus status,
            OffsetDateTime paidAt,
            OffsetDateTime shippedAt,
            OffsetDateTime completedAt,
            OffsetDateTime cancelledAt,
            List<OrderItemRequest> items);

    boolean deleteById(UUID id);
}
