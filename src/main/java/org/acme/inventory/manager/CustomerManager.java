package org.acme.inventory.manager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Customer;
import org.acme.inventory.dto.customer.CustomerRequest;

public interface CustomerManager {

    List<Customer> getCustomers();

    Optional<Customer> getCustomerById(UUID id);

    Customer createCustomer(CustomerRequest request);

    Optional<Customer> updateCustomer(UUID id, CustomerRequest request);

    boolean deleteCustomer(UUID id);
}
