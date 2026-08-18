package org.acme.inventory.manager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Customer;
import org.acme.inventory.dto.customer.CustomerRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;

public interface CustomerManager {

    List<Customer> getCustomers();

    PageResult<Customer> getCustomers(PageQuery query);

    long count();

    Optional<Customer> getCustomerById(UUID id);

    Customer createCustomer(CustomerRequest request);

    Optional<Customer> updateCustomer(UUID id, CustomerRequest request);

    boolean deleteCustomer(UUID id);
}
