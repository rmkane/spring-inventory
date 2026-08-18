package org.acme.inventory.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Customer;
import org.acme.inventory.dto.customer.CustomerRequest;
import org.acme.inventory.repository.CustomerRepository;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private static final RowMapper<Customer> CUSTOMER_MAPPER = DataClassRowMapper.newInstance(Customer.class);

    private static final String SELECT_CUSTOMERS = """
            SELECT id, name, email, created_at, updated_at
            FROM customers
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query(SELECT_CUSTOMERS, CUSTOMER_MAPPER);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        List<Customer> customers = jdbcTemplate.query(
                SELECT_CUSTOMERS + " WHERE id = :id",
                new MapSqlParameterSource("id", id),
                CUSTOMER_MAPPER);
        return customers.stream().findFirst();
    }

    @Override
    public Customer insert(CustomerRequest request) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                """
                        INSERT INTO customers (id, name, email)
                        VALUES (:id, :name, :email)
                        """,
                customerParams(id, request));
        return findById(id).orElseThrow();
    }

    @Override
    public Optional<Customer> update(UUID id, CustomerRequest request) {
        int updated = jdbcTemplate.update(
                """
                        UPDATE customers
                        SET name = :name, email = :email
                        WHERE id = :id
                        """,
                customerParams(id, request));
        if (updated == 0) {
            return Optional.empty();
        }
        return findById(id);
    }

    @Override
    public boolean deleteById(UUID id) {
        return jdbcTemplate.update(
                "DELETE FROM customers WHERE id = :id",
                new MapSqlParameterSource("id", id)) > 0;
    }

    private static MapSqlParameterSource customerParams(UUID id, CustomerRequest request) {
        return new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("name", request.name())
                .addValue("email", request.email());
    }
}
