package org.acme.inventory.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import org.acme.inventory.domain.Customer;
import org.acme.inventory.dto.customer.CustomerRequest;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.repository.CustomerRepository;
import org.acme.inventory.repository.sql.SqlDateTimes;
import org.acme.inventory.repository.sql.SqlPaging;
import org.acme.inventory.repository.sql.SqlRowMapper;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private static final String SELECT_CUSTOMERS = """
            SELECT id, name, email, created_at, updated_at
            FROM customers
            """;

    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "name", "name",
            "email", "email",
            "carts", "(SELECT count(*) FROM carts c WHERE c.customer_id = customers.id)",
            "orders", "(SELECT count(*) FROM orders o WHERE o.customer_id = customers.id)",
            "createdAt", "created_at",
            "updatedAt", "updated_at");

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<Customer> customerMapper;

    public CustomerRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate, SqlDateTimes sqlDateTimes) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerMapper = new CustomerMapper(sqlDateTimes);
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query(SELECT_CUSTOMERS, customerMapper);
    }

    @Override
    public PageResult<Customer> findPage(PageQuery query) {
        return SqlPaging.fetch(
                jdbcTemplate,
                "SELECT count(*) FROM customers",
                SELECT_CUSTOMERS,
                query,
                SORT_COLUMNS,
                "name",
                "asc",
                customerMapper);
    }

    @Override
    public long count() {
        return SqlPaging.count(jdbcTemplate, "SELECT count(*) FROM customers");
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        List<Customer> customers = jdbcTemplate.query(
                SELECT_CUSTOMERS + " WHERE id = :id",
                new MapSqlParameterSource("id", id),
                customerMapper);
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

    private static final class CustomerMapper extends SqlRowMapper<Customer> {
        private CustomerMapper(SqlDateTimes sqlDateTimes) {
            super(sqlDateTimes);
        }

        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Customer(
                    getUuid(rs, "id"),
                    getString(rs, "name"),
                    getString(rs, "email"),
                    getOffsetDateTime(rs, "created_at"),
                    getOffsetDateTime(rs, "updated_at"));
        }
    }
}
