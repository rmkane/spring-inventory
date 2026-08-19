package org.acme.inventory.repository.mapper.cart;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import org.acme.inventory.repository.mapper.SqlRowMapper;
import org.acme.inventory.repository.sql.SqlDateTimes;

@Component
public class CartRowMapper extends SqlRowMapper<CartRow> {

    public CartRowMapper(SqlDateTimes sqlDateTimes) {
        super(sqlDateTimes);
    }

    @Override
    public CartRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CartRow(
                getUuid(rs, "id"),
                getUuid(rs, "customer_id"),
                getOffsetDateTime(rs, "created_at"),
                getOffsetDateTime(rs, "updated_at"));
    }
}
