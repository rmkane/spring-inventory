package org.acme.inventory.repository.mapper.customer;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import org.acme.inventory.domain.Customer;
import org.acme.inventory.repository.mapper.SqlRowMapper;
import org.acme.inventory.repository.sql.SqlDateTimes;

@Component
public class CustomerRowMapper extends SqlRowMapper<Customer> {

    public CustomerRowMapper(SqlDateTimes sqlDateTimes) {
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
