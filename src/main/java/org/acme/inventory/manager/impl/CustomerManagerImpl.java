package org.acme.inventory.manager.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Customer;
import org.acme.inventory.dto.customer.CustomerRequest;
import org.acme.inventory.manager.CustomerManager;
import org.acme.inventory.repository.CustomerRepository;

@Service
@RequiredArgsConstructor
public class CustomerManagerImpl implements CustomerManager {

    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getCustomerById(UUID id) {
        return customerRepository.findById(id);
    }

    @Override
    @Transactional
    public Customer createCustomer(CustomerRequest request) {
        return customerRepository.insert(request);
    }

    @Override
    @Transactional
    public Optional<Customer> updateCustomer(UUID id, CustomerRequest request) {
        return customerRepository.update(id, request);
    }

    @Override
    @Transactional
    public boolean deleteCustomer(UUID id) {
        return customerRepository.deleteById(id);
    }
}
