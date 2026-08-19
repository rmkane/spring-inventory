package org.acme.inventory.repository.mapper.product;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import org.acme.inventory.domain.Product;
import org.acme.inventory.repository.mapper.SqlRowMapper;
import org.acme.inventory.repository.sql.SqlDateTimes;

@Component
public class ProductRowMapper extends SqlRowMapper<Product> {

    public ProductRowMapper(SqlDateTimes sqlDateTimes) {
        super(sqlDateTimes);
    }

    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Product(
                getUuid(rs, "id"),
                getString(rs, "name"),
                getString(rs, "description"),
                getBigDecimal(rs, "price"),
                getInt(rs, "quantity_on_hand"),
                getInt(rs, "quantity_reserved"),
                getOffsetDateTime(rs, "created_at"),
                getOffsetDateTime(rs, "updated_at"));
    }
}
