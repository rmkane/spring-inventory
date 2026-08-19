package org.acme.inventory.repository;

import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Customer;
import org.acme.inventory.dto.customer.CustomerRequest;

public interface CustomerRepository extends JdbcRepository<Customer, UUID> {

    Customer insert(CustomerRequest request);

    Optional<Customer> update(UUID id, CustomerRequest request);
}
