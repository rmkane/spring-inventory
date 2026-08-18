package org.acme.inventory.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import org.acme.inventory.domain.Customer;
import org.acme.inventory.dto.customer.CustomerResponse;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getCreatedAt(),
                customer.getUpdatedAt());
    }

    public List<CustomerResponse> toResponses(List<Customer> customers) {
        // spotless:off
        return customers.stream()
            .map(this::toResponse)
            .toList();
        // spotless:on
    }
}
