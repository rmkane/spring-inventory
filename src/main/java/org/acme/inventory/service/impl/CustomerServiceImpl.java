package org.acme.inventory.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Customer;
import org.acme.inventory.dto.customer.CustomerRequest;
import org.acme.inventory.exception.ResourceNotFoundException;
import org.acme.inventory.manager.CustomerManager;
import org.acme.inventory.service.CustomerService;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerManager customerManager;

    @Override
    public List<Customer> getCustomers() {
        return customerManager.getCustomers();
    }

    @Override
    public Customer getCustomerById(UUID id) {
        return ResourceNotFoundException.require(customerManager.getCustomerById(id), "Customer", id);
    }

    @Override
    public Customer createCustomer(CustomerRequest request) {
        return customerManager.createCustomer(request);
    }

    @Override
    public Customer updateCustomer(UUID id, CustomerRequest request) {
        return ResourceNotFoundException.require(customerManager.updateCustomer(id, request), "Customer", id);
    }

    @Override
    public void deleteCustomer(UUID id) {
        ResourceNotFoundException.requireDeleted(customerManager.deleteCustomer(id), "Customer", id);
    }
}
