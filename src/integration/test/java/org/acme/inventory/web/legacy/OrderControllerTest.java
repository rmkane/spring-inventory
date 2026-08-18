package org.acme.inventory.web.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import org.acme.inventory.domain.OrderStatus;
import org.acme.inventory.dto.customer.CustomerRequest;
import org.acme.inventory.dto.customer.CustomerResponse;
import org.acme.inventory.dto.order.OrderItemRequest;
import org.acme.inventory.dto.order.OrderRequest;
import org.acme.inventory.dto.order.OrderResponse;
import org.acme.inventory.dto.page.PageResponse;
import org.acme.inventory.dto.product.ProductRequest;
import org.acme.inventory.dto.product.ProductResponse;

@Tag("integration")
@Tag("legacy")
public class OrderControllerTest extends TestSuite {

    @Test
    public void testGetAllOrders() {
        ResponseEntity<PageResponse<OrderResponse>> response = get(
                "/orders",
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().content().isEmpty());
        write(response.getBody(), "get-all-orders.json");
    }

    @Test
    public void testOrderCrud() {
        CustomerResponse customer = post(
                "/customers",
                new CustomerRequest("IT Order Customer", "it-order-" + UUID.randomUUID() + "@example.com"),
                CustomerResponse.class)
                .getBody();
        ProductResponse product = post(
                "/products",
                new ProductRequest("IT Order Product", null, new BigDecimal("15.00")),
                ProductResponse.class)
                .getBody();

        OrderRequest createRequest = new OrderRequest(
                customer.id(),
                null,
                List.of(new OrderItemRequest(product.id(), 1, null)));
        ResponseEntity<OrderResponse> created = post("/orders", createRequest, OrderResponse.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        OrderResponse order = created.getBody();
        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.status());
        assertEquals(1, order.items().size());
        assertEquals(0, order.items().get(0).unitPrice().compareTo(new BigDecimal("15.00")));
        assertNull(order.paidAt());
        write(order, "create-order.json");

        ResponseEntity<OrderResponse> fetched = get("/orders/" + order.id(), OrderResponse.class);
        assertEquals(HttpStatus.OK, fetched.getStatusCode());
        assertEquals(order.id(), fetched.getBody().id());

        OrderRequest updateRequest = new OrderRequest(
                customer.id(),
                OrderStatus.PAID,
                List.of(new OrderItemRequest(product.id(), 2, null)));
        ResponseEntity<OrderResponse> updated = put("/orders/" + order.id(), updateRequest, OrderResponse.class);
        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertEquals(OrderStatus.PAID, updated.getBody().status());
        assertEquals(2, updated.getBody().items().get(0).quantity());
        assertNotNull(updated.getBody().paidAt());
        write(updated.getBody(), "update-order.json");

        ResponseEntity<Void> deleted = delete("/orders/" + order.id());
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());

        ResponseEntity<ProblemDetail> missing = get("/orders/" + order.id(), ProblemDetail.class);
        assertEquals(HttpStatus.NOT_FOUND, missing.getStatusCode());

        delete("/products/" + product.id());
        delete("/customers/" + customer.id());
    }
}
