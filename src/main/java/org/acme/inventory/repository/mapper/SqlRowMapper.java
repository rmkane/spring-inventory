package org.acme.inventory.repository.mapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

import org.acme.inventory.repository.sql.SqlDateTimes;

public abstract class SqlRowMapper<T> implements RowMapper<T> {

    private final SqlDateTimes sqlDateTimes;

    protected SqlRowMapper(SqlDateTimes sqlDateTimes) {
        this.sqlDateTimes = sqlDateTimes;
    }

    protected UUID getUuid(ResultSet rs, String column) throws SQLException {
        return rs.getObject(column, UUID.class);
    }

    protected String getString(ResultSet rs, String column) throws SQLException {
        return rs.getString(column);
    }

    protected int getInt(ResultSet rs, String column) throws SQLException {
        return rs.getInt(column);
    }

    protected BigDecimal getBigDecimal(ResultSet rs, String column) throws SQLException {
        return rs.getBigDecimal(column);
    }

    protected OffsetDateTime getOffsetDateTime(ResultSet rs, String column) throws SQLException {
        return sqlDateTimes.offsetDateTime(rs, column);
    }

    protected LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        return sqlDateTimes.localDateTime(rs, column);
    }

    protected LocalDate getLocalDate(ResultSet rs, String column) throws SQLException {
        return sqlDateTimes.localDate(rs, column);
    }

    protected LocalTime getLocalTime(ResultSet rs, String column) throws SQLException {
        return sqlDateTimes.localTime(rs, column);
    }

    protected OffsetTime getOffsetTime(ResultSet rs, String column) throws SQLException {
        return sqlDateTimes.offsetTime(rs, column);
    }

    protected Instant getInstant(ResultSet rs, String column) throws SQLException {
        return sqlDateTimes.instant(rs, column);
    }

    protected ZonedDateTime getZonedDateTime(ResultSet rs, String column) throws SQLException {
        return sqlDateTimes.zonedDateTime(rs, column);
    }

    protected <E extends Enum<E>> E getEnum(ResultSet rs, String column, Class<E> type) throws SQLException {
        String value = rs.getString(column);
        return value == null ? null : Enum.valueOf(type, value);
    }
}
