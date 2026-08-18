package org.acme.inventory.service;

import java.util.List;
import java.util.UUID;

import org.acme.inventory.domain.Customer;
import org.acme.inventory.dto.customer.CustomerRequest;

public interface CustomerService {

    List<Customer> getCustomers();

    Customer getCustomerById(UUID id);

    Customer createCustomer(CustomerRequest request);

    Customer updateCustomer(UUID id, CustomerRequest request);

    void deleteCustomer(UUID id);
}
