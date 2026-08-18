package org.acme.inventory.service;

import java.util.List;
import java.util.UUID;

import org.acme.inventory.domain.Order;
import org.acme.inventory.dto.order.OrderRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;

public interface OrderService {

    List<Order> getOrders();

    PageResult<Order> getOrders(PageQuery query);

    long count();

    Order getOrderById(UUID id);

    List<Order> getOrdersByCustomerId(UUID customerId);

    Order createOrder(OrderRequest request);

    Order updateOrder(UUID id, OrderRequest request);

    void deleteOrder(UUID id);
}
