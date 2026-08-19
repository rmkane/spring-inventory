package org.acme.inventory.repository.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

class SqlRowMapperTest {

    @Test
    void temporalGettersDelegateToSqlDateTimes() throws SQLException {
        OffsetDateTime paidAt = OffsetDateTime.of(2026, 8, 18, 20, 4, 0, 0, ZoneOffset.UTC);
        LocalDateTime createdAt = LocalDateTime.of(2026, 8, 18, 20, 4);
        LocalDate shipDate = LocalDate.of(2026, 8, 19);
        LocalTime opensAt = LocalTime.of(9, 0);
        OffsetTime shiftEnd = OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC);
        Instant occurredAt = paidAt.toInstant();
        ZonedDateTime zonedAt = paidAt.toZonedDateTime();

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getObject("paid_at", OffsetDateTime.class)).thenReturn(paidAt);
        when(resultSet.getObject("created_at", LocalDateTime.class)).thenReturn(createdAt);
        when(resultSet.getObject("ship_date", LocalDate.class)).thenReturn(shipDate);
        when(resultSet.getObject("opens_at", LocalTime.class)).thenReturn(opensAt);
        when(resultSet.getObject("shift_end", OffsetTime.class)).thenReturn(shiftEnd);
        when(resultSet.getObject("occurred_at", Instant.class)).thenReturn(occurredAt);
        when(resultSet.getObject("zoned_at", ZonedDateTime.class)).thenReturn(zonedAt);

        TemporalRow row = new TemporalRowMapper(new SqlDateTimes("PostgreSQL")).mapRow(resultSet, 1);

        assertEquals(paidAt, row.paidAt());
        assertEquals(createdAt, row.createdAt());
        assertEquals(shipDate, row.shipDate());
        assertEquals(opensAt, row.opensAt());
        assertEquals(shiftEnd, row.shiftEnd());
        assertEquals(occurredAt, row.occurredAt());
        assertEquals(zonedAt, row.zonedAt());
    }

    private record TemporalRow(
            OffsetDateTime paidAt,
            LocalDateTime createdAt,
            LocalDate shipDate,
            LocalTime opensAt,
            OffsetTime shiftEnd,
            Instant occurredAt,
            ZonedDateTime zonedAt) {
    }

    private static final class TemporalRowMapper extends SqlRowMapper<TemporalRow> {
        private TemporalRowMapper(SqlDateTimes sqlDateTimes) {
            super(sqlDateTimes);
        }

        @Override
        public TemporalRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TemporalRow(
                    getOffsetDateTime(rs, "paid_at"),
                    getLocalDateTime(rs, "created_at"),
                    getLocalDate(rs, "ship_date"),
                    getLocalTime(rs, "opens_at"),
                    getOffsetTime(rs, "shift_end"),
                    getInstant(rs, "occurred_at"),
                    getZonedDateTime(rs, "zoned_at"));
        }
    }
}
