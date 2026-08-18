package org.acme.inventory.manager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Order;
import org.acme.inventory.dto.order.OrderRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;

public interface OrderManager {

    List<Order> getOrders();

    PageResult<Order> getOrders(PageQuery query);

    long count();

    Optional<Order> getOrderById(UUID id);

    List<Order> getOrdersByCustomerId(UUID customerId);

    Order createOrder(OrderRequest request);

    Optional<Order> updateOrder(UUID id, OrderRequest request);

    boolean deleteOrder(UUID id);
}
