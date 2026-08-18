package org.acme.inventory.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import org.acme.inventory.domain.Customer;
import org.acme.inventory.dto.customer.CustomerResponse;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.id(),
                customer.name(),
                customer.email(),
                customer.createdAt(),
                customer.updatedAt());
    }

    public List<CustomerResponse> toResponses(List<Customer> customers) {
        return customers.stream().map(this::toResponse).toList();
    }
}
