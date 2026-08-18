package org.acme.inventory.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import org.acme.inventory.domain.Order;
import org.acme.inventory.domain.OrderItem;
import org.acme.inventory.dto.order.OrderItemResponse;
import org.acme.inventory.dto.order.OrderResponse;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getPaidAt(),
                order.getShippedAt(),
                order.getCompletedAt(),
                order.getCancelledAt(),
                order.getItems().stream().map(this::toItemResponse).toList());
    }

    public List<OrderResponse> toResponses(List<Order> orders) {
        return orders.stream().map(this::toResponse).toList();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice());
    }
}
