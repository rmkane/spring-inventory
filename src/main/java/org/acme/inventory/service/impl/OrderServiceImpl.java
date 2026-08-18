package org.acme.inventory.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Order;
import org.acme.inventory.dto.order.OrderItemRequest;
import org.acme.inventory.dto.order.OrderRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.exception.ResourceNotFoundException;
import org.acme.inventory.manager.OrderManager;
import org.acme.inventory.manager.ProductManager;
import org.acme.inventory.service.OrderService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderManager orderManager;
    private final ProductManager productManager;

    @Override
    public List<Order> getOrders() {
        return orderManager.getOrders();
    }

    @Override
    public PageResult<Order> getOrders(PageQuery query) {
        return orderManager.getOrders(query);
    }

    @Override
    public long count() {
        return orderManager.count();
    }

    @Override
    public Order getOrderById(UUID id) {
        return ResourceNotFoundException.require(orderManager.getOrderById(id), "Order", id);
    }

    @Override
    public List<Order> getOrdersByCustomerId(UUID customerId) {
        return orderManager.getOrdersByCustomerId(customerId);
    }

    @Override
    public Order createOrder(OrderRequest request) {
        return orderManager.createOrder(withCatalogPrices(request));
    }

    @Override
    public Order updateOrder(UUID id, OrderRequest request) {
        return ResourceNotFoundException.require(
                orderManager.updateOrder(id, withCatalogPrices(request)), "Order", id);
    }

    @Override
    public void deleteOrder(UUID id) {
        ResourceNotFoundException.requireDeleted(orderManager.deleteOrder(id), "Order", id);
    }

    private OrderRequest withCatalogPrices(OrderRequest request) {
        List<OrderItemRequest> items = request.items().stream().map(this::withUnitPrice).toList();
        return new OrderRequest(request.customerId(), request.status(), items);
    }

    private OrderItemRequest withUnitPrice(OrderItemRequest item) {
        if (item.unitPrice() != null) {
            return item;
        }
        BigDecimal price = ResourceNotFoundException
                .require(productManager.getProductById(item.productId()), "Product", item.productId())
                .price();
        return new OrderItemRequest(item.productId(), item.quantity(), price);
    }
}
