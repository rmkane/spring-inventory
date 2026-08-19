package org.acme.inventory.repository.mapper.order;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import org.acme.inventory.repository.mapper.SqlRowMapper;
import org.acme.inventory.repository.sql.SqlDateTimes;

@Component
public class OrderItemRowMapper extends SqlRowMapper<OrderItemRow> {

    public OrderItemRowMapper(SqlDateTimes sqlDateTimes) {
        super(sqlDateTimes);
    }

    @Override
    public OrderItemRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OrderItemRow(
                getUuid(rs, "order_id"),
                getUuid(rs, "product_id"),
                getString(rs, "product_name"),
                getInt(rs, "quantity"),
                getBigDecimal(rs, "unit_price"));
    }
}
