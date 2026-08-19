package org.acme.inventory.repository.mapper.cart;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import org.acme.inventory.repository.mapper.SqlRowMapper;
import org.acme.inventory.repository.sql.SqlDateTimes;

@Component
public class CartItemRowMapper extends SqlRowMapper<CartItemRow> {

    public CartItemRowMapper(SqlDateTimes sqlDateTimes) {
        super(sqlDateTimes);
    }

    @Override
    public CartItemRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CartItemRow(
                getUuid(rs, "cart_id"),
                getUuid(rs, "product_id"),
                getString(rs, "product_name"),
                getInt(rs, "quantity"),
                getBigDecimal(rs, "unit_price"),
                getOffsetDateTime(rs, "added_at"),
                getOffsetDateTime(rs, "updated_at"));
    }
}
