package org.acme.inventory.web.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import org.acme.inventory.dto.cart.CartResponse;
import org.acme.inventory.dto.customer.CustomerRequest;
import org.acme.inventory.dto.customer.CustomerResponse;
import org.acme.inventory.dto.order.OrderResponse;
import org.acme.inventory.dto.page.PageResponse;

@Tag("integration")
@Tag("legacy")
public class CustomerControllerTest extends TestSuite {

    @Test
    public void testGetAllCustomers() {
        ResponseEntity<PageResponse<CustomerResponse>> response = get(
                "/customers",
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().content().isEmpty());
        write(response.getBody(), "get-all-customers.json");
    }

    @Test
    public void testCustomerCrud() {
        String email = "it-" + UUID.randomUUID() + "@example.com";
        CustomerRequest createRequest = new CustomerRequest("IT Customer", email);
        ResponseEntity<CustomerResponse> created = post("/customers", createRequest, CustomerResponse.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        CustomerResponse customer = created.getBody();
        assertNotNull(customer);
        assertEquals("IT Customer", customer.name());
        assertEquals(email, customer.email());
        write(customer, "create-customer.json");

        ResponseEntity<CustomerResponse> fetched = get("/customers/" + customer.id(), CustomerResponse.class);
        assertEquals(HttpStatus.OK, fetched.getStatusCode());
        assertEquals(customer.id(), fetched.getBody().id());

        CustomerRequest updateRequest = new CustomerRequest("IT Customer Updated", email);
        ResponseEntity<CustomerResponse> updated = put(
                "/customers/" + customer.id(),
                updateRequest,
                CustomerResponse.class);
        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertEquals("IT Customer Updated", updated.getBody().name());
        write(updated.getBody(), "update-customer.json");

        ResponseEntity<List<CartResponse>> carts = get(
                "/customers/" + customer.id() + "/carts",
                new ParameterizedTypeReference<>() {
                });
        assertEquals(HttpStatus.OK, carts.getStatusCode());
        assertTrue(carts.getBody().isEmpty());

        ResponseEntity<List<OrderResponse>> orders = get(
                "/customers/" + customer.id() + "/orders",
                new ParameterizedTypeReference<>() {
                });
        assertEquals(HttpStatus.OK, orders.getStatusCode());
        assertTrue(orders.getBody().isEmpty());

        ResponseEntity<Void> deleted = delete("/customers/" + customer.id());
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());

        ResponseEntity<ProblemDetail> missing = get("/customers/" + customer.id(), ProblemDetail.class);
        assertEquals(HttpStatus.NOT_FOUND, missing.getStatusCode());
    }
}
