package org.acme.inventory.repository.mapper.inventory;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import org.acme.inventory.domain.Inventory;
import org.acme.inventory.repository.mapper.SqlRowMapper;
import org.acme.inventory.repository.sql.SqlDateTimes;

@Component
public class InventoryRowMapper extends SqlRowMapper<Inventory> {

    public InventoryRowMapper(SqlDateTimes sqlDateTimes) {
        super(sqlDateTimes);
    }

    @Override
    public Inventory mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Inventory(
                getUuid(rs, "product_id"),
                getInt(rs, "quantity_on_hand"),
                getInt(rs, "quantity_reserved"),
                getOffsetDateTime(rs, "updated_at"));
    }
}
