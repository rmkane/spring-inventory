package org.acme.inventory.manager.impl;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Order;
import org.acme.inventory.domain.OrderStatus;
import org.acme.inventory.dto.order.OrderItemRequest;
import org.acme.inventory.dto.order.OrderRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.manager.OrderManager;
import org.acme.inventory.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderManagerImpl implements OrderManager {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public PageResult<Order> getOrders(PageQuery query) {
        return orderRepository.findPage(query);
    }

    @Override
    public long count() {
        return orderRepository.count();
    }

    @Override
    public Optional<Order> getOrderById(UUID id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getOrdersByCustomerId(UUID customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional
    public Order createOrder(OrderRequest request) {
        OrderStatus status = request.status() == null ? OrderStatus.PENDING : request.status();
        StatusTimes times = StatusTimes.from(status, null);
        return orderRepository.insert(
                request.customerId(),
                status,
                times.paidAt(),
                times.shippedAt(),
                times.completedAt(),
                times.cancelledAt(),
                mergeItems(request.items()));
    }

    @Override
    @Transactional
    public Optional<Order> updateOrder(UUID id, OrderRequest request) {
        return orderRepository.findById(id).flatMap(existing -> {
            OrderStatus status = request.status() == null ? existing.status() : request.status();
            StatusTimes times = StatusTimes.from(status, existing);
            return orderRepository.update(
                    id,
                    request.customerId(),
                    status,
                    times.paidAt(),
                    times.shippedAt(),
                    times.completedAt(),
                    times.cancelledAt(),
                    mergeItems(request.items()));
        });
    }

    @Override
    @Transactional
    public boolean deleteOrder(UUID id) {
        return orderRepository.deleteById(id);
    }

    private static List<OrderItemRequest> mergeItems(List<OrderItemRequest> items) {
        return items.stream()
                .collect(Collectors.toMap(
                        OrderItemRequest::productId,
                        item -> item,
                        (left, right) -> new OrderItemRequest(
                                left.productId(),
                                left.quantity() + right.quantity(),
                                left.unitPrice() != null ? left.unitPrice() : right.unitPrice()),
                        LinkedHashMap::new))
                .values()
                .stream()
                .toList();
    }

    private record StatusTimes(
            OffsetDateTime paidAt,
            OffsetDateTime shippedAt,
            OffsetDateTime completedAt,
            OffsetDateTime cancelledAt) {

        private static StatusTimes from(OrderStatus status, Order existing) {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime paidAt = existing == null ? null : existing.paidAt();
            OffsetDateTime shippedAt = existing == null ? null : existing.shippedAt();
            OffsetDateTime completedAt = existing == null ? null : existing.completedAt();
            OffsetDateTime cancelledAt = existing == null ? null : existing.cancelledAt();

            return switch (status) {
            case PAID -> new StatusTimes(orNow(paidAt, now), shippedAt, completedAt, cancelledAt);
            case SHIPPED -> new StatusTimes(orNow(paidAt, now), orNow(shippedAt, now), completedAt, cancelledAt);
            case COMPLETED -> new StatusTimes(
                    orNow(paidAt, now), orNow(shippedAt, now), orNow(completedAt, now), cancelledAt);
            case CANCELLED -> new StatusTimes(paidAt, shippedAt, completedAt, orNow(cancelledAt, now));
            case PENDING, PROCESSING -> new StatusTimes(paidAt, shippedAt, completedAt, cancelledAt);
            };
        }

        private static OffsetDateTime orNow(OffsetDateTime current, OffsetDateTime now) {
            return current != null ? current : now;
        }
    }
}
