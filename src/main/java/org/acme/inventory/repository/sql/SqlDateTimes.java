package org.acme.inventory.repository.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * JDBC temporal bind/read helper. Postgres accepts {@code java.time} directly;
 * other products fall back to {@code java.sql} date/time types.
 */
@Slf4j
@Component
public class SqlDateTimes implements InitializingBean {

    private final DataSource dataSource;

    private String databaseProductName = "unknown";
    private boolean nativeJavaTime;

    @Autowired
    public SqlDateTimes(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Test constructor that skips the DataSource probe.
     */
    SqlDateTimes(String databaseProductName) {
        this.dataSource = null;
        applyDatabaseProduct(databaseProductName);
    }

    @Override
    public void afterPropertiesSet() {
        if (dataSource == null) {
            return;
        }
        try (Connection connection = dataSource.getConnection()) {
            applyDatabaseProduct(connection.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            throw new IllegalStateException("Could not read JDBC database product name", e);
        }
        log.info(
                "JDBC temporal binding: {} (native java.time: {})",
                databaseProductName,
                nativeJavaTime);
    }

    public boolean nativeJavaTime() {
        return nativeJavaTime;
    }

    public String databaseProductName() {
        return databaseProductName;
    }

    public Object bind(OffsetDateTime value) {
        if (value == null || nativeJavaTime) {
            return value;
        }
        return Timestamp.from(value.toInstant());
    }

    public Object bind(LocalDateTime value) {
        if (value == null || nativeJavaTime) {
            return value;
        }
        return Timestamp.valueOf(value);
    }

    public Object bind(LocalDate value) {
        if (value == null || nativeJavaTime) {
            return value;
        }
        return Date.valueOf(value);
    }

    public Object bind(LocalTime value) {
        if (value == null || nativeJavaTime) {
            return value;
        }
        return Time.valueOf(value);
    }

    public Object bind(OffsetTime value) {
        if (value == null || nativeJavaTime) {
            return value;
        }
        return Time.valueOf(value.toLocalTime());
    }

    public Object bind(Instant value) {
        if (value == null || nativeJavaTime) {
            return value;
        }
        return Timestamp.from(value);
    }

    public Object bind(ZonedDateTime value) {
        if (value == null || nativeJavaTime) {
            return value;
        }
        return Timestamp.from(value.toInstant());
    }

    public OffsetDateTime offsetDateTime(ResultSet resultSet, String column) throws SQLException {
        if (nativeJavaTime) {
            return resultSet.getObject(column, OffsetDateTime.class);
        }
        Timestamp timestamp = resultSet.getTimestamp(column);
        return timestamp == null ? null : OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC);
    }

    public LocalDateTime localDateTime(ResultSet resultSet, String column) throws SQLException {
        if (nativeJavaTime) {
            return resultSet.getObject(column, LocalDateTime.class);
        }
        Timestamp timestamp = resultSet.getTimestamp(column);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    public LocalDate localDate(ResultSet resultSet, String column) throws SQLException {
        if (nativeJavaTime) {
            return resultSet.getObject(column, LocalDate.class);
        }
        Date date = resultSet.getDate(column);
        return date == null ? null : date.toLocalDate();
    }

    public LocalTime localTime(ResultSet resultSet, String column) throws SQLException {
        if (nativeJavaTime) {
            return resultSet.getObject(column, LocalTime.class);
        }
        Time time = resultSet.getTime(column);
        return time == null ? null : time.toLocalTime();
    }

    public OffsetTime offsetTime(ResultSet resultSet, String column) throws SQLException {
        if (nativeJavaTime) {
            return resultSet.getObject(column, OffsetTime.class);
        }
        Time time = resultSet.getTime(column);
        return time == null ? null : OffsetTime.of(time.toLocalTime(), ZoneOffset.UTC);
    }

    public Instant instant(ResultSet resultSet, String column) throws SQLException {
        if (nativeJavaTime) {
            return resultSet.getObject(column, Instant.class);
        }
        Timestamp timestamp = resultSet.getTimestamp(column);
        return timestamp == null ? null : timestamp.toInstant();
    }

    public ZonedDateTime zonedDateTime(ResultSet resultSet, String column) throws SQLException {
        if (nativeJavaTime) {
            return resultSet.getObject(column, ZonedDateTime.class);
        }
        Timestamp timestamp = resultSet.getTimestamp(column);
        return timestamp == null ? null : ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC);
    }

    private void applyDatabaseProduct(String productName) {
        this.databaseProductName = productName == null || productName.isBlank() ? "unknown" : productName;
        this.nativeJavaTime = this.databaseProductName.toLowerCase(Locale.ROOT).contains("postgres");
    }
}
