package org.acme.inventory.repository.mapper.order;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import org.acme.inventory.domain.OrderStatus;
import org.acme.inventory.repository.mapper.SqlRowMapper;
import org.acme.inventory.repository.sql.SqlDateTimes;

@Component
public class OrderRowMapper extends SqlRowMapper<OrderRow> {

    public OrderRowMapper(SqlDateTimes sqlDateTimes) {
        super(sqlDateTimes);
    }

    @Override
    public OrderRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OrderRow(
                getUuid(rs, "id"),
                getUuid(rs, "customer_id"),
                getEnum(rs, "status", OrderStatus.class),
                getOffsetDateTime(rs, "created_at"),
                getOffsetDateTime(rs, "updated_at"),
                getOffsetDateTime(rs, "paid_at"),
                getOffsetDateTime(rs, "shipped_at"),
                getOffsetDateTime(rs, "completed_at"),
                getOffsetDateTime(rs, "cancelled_at"));
    }
}
