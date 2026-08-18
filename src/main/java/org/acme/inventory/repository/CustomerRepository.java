package org.acme.inventory.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.inventory.domain.Customer;
import org.acme.inventory.dto.customer.CustomerRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;

public interface CustomerRepository {

    List<Customer> findAll();

    PageResult<Customer> findPage(PageQuery query);

    long count();

    Optional<Customer> findById(UUID id);

    Customer insert(CustomerRequest request);

    Optional<Customer> update(UUID id, CustomerRequest request);

    boolean deleteById(UUID id);
}
