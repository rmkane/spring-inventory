package org.acme.inventory.repository.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

import org.junit.jupiter.api.Test;

class SqlDateTimesTest {

    private static final ZoneOffset OFFSET = ZoneOffset.ofHours(-4);
    private static final LocalDate LOCAL_DATE = LocalDate.of(2026, 8, 18);
    private static final LocalTime LOCAL_TIME = LocalTime.of(20, 4, 0);
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(LOCAL_DATE, LOCAL_TIME);
    private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.of(LOCAL_DATE_TIME, OFFSET);
    private static final OffsetTime OFFSET_TIME = OffsetTime.of(LOCAL_TIME, OFFSET);
    private static final Instant INSTANT = OFFSET_DATE_TIME.toInstant();
    private static final ZonedDateTime ZONED_DATE_TIME = OFFSET_DATE_TIME.toZonedDateTime();

    @Test
    void postgresPassesJavaTimeThrough() {
        SqlDateTimes sqlDateTimes = new SqlDateTimes("PostgreSQL");

        assertTrue(sqlDateTimes.nativeJavaTime());
        assertEquals("PostgreSQL", sqlDateTimes.databaseProductName());
        assertSame(OFFSET_DATE_TIME, sqlDateTimes.bind(OFFSET_DATE_TIME));
        assertSame(LOCAL_DATE_TIME, sqlDateTimes.bind(LOCAL_DATE_TIME));
        assertSame(LOCAL_DATE, sqlDateTimes.bind(LOCAL_DATE));
        assertSame(LOCAL_TIME, sqlDateTimes.bind(LOCAL_TIME));
        assertSame(OFFSET_TIME, sqlDateTimes.bind(OFFSET_TIME));
        assertSame(INSTANT, sqlDateTimes.bind(INSTANT));
        assertSame(ZONED_DATE_TIME, sqlDateTimes.bind(ZONED_DATE_TIME));
        assertNull(sqlDateTimes.bind((OffsetDateTime) null));
        assertNull(sqlDateTimes.bind((LocalDateTime) null));
        assertNull(sqlDateTimes.bind((LocalTime) null));
        assertNull(sqlDateTimes.bind((OffsetTime) null));
        assertNull(sqlDateTimes.bind((ZonedDateTime) null));
    }

    @Test
    void otherProductsConvertToSqlTypes() {
        SqlDateTimes sqlDateTimes = new SqlDateTimes("H2");

        assertFalse(sqlDateTimes.nativeJavaTime());
        assertEquals(Timestamp.from(INSTANT), sqlDateTimes.bind(OFFSET_DATE_TIME));
        assertEquals(Timestamp.valueOf(LOCAL_DATE_TIME), sqlDateTimes.bind(LOCAL_DATE_TIME));
        assertEquals(java.sql.Date.valueOf(LOCAL_DATE), sqlDateTimes.bind(LOCAL_DATE));
        assertEquals(Time.valueOf(LOCAL_TIME), sqlDateTimes.bind(LOCAL_TIME));
        assertEquals(Time.valueOf(LOCAL_TIME), sqlDateTimes.bind(OFFSET_TIME));
        assertEquals(Timestamp.from(INSTANT), sqlDateTimes.bind(INSTANT));
        assertEquals(Timestamp.from(INSTANT), sqlDateTimes.bind(ZONED_DATE_TIME));
    }

    @Test
    void postgresReadsJavaTimeFromResultSet() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getObject("paid_at", OffsetDateTime.class)).thenReturn(OFFSET_DATE_TIME);
        when(resultSet.getObject("created_at", LocalDateTime.class)).thenReturn(LOCAL_DATE_TIME);
        when(resultSet.getObject("ship_date", LocalDate.class)).thenReturn(LOCAL_DATE);
        when(resultSet.getObject("opens_at", LocalTime.class)).thenReturn(LOCAL_TIME);
        when(resultSet.getObject("shift_end", OffsetTime.class)).thenReturn(OFFSET_TIME);
        when(resultSet.getObject("occurred_at", Instant.class)).thenReturn(INSTANT);
        when(resultSet.getObject("zoned_at", ZonedDateTime.class)).thenReturn(ZONED_DATE_TIME);

        SqlDateTimes sqlDateTimes = new SqlDateTimes("PostgreSQL");

        assertEquals(OFFSET_DATE_TIME, sqlDateTimes.offsetDateTime(resultSet, "paid_at"));
        assertEquals(LOCAL_DATE_TIME, sqlDateTimes.localDateTime(resultSet, "created_at"));
        assertEquals(LOCAL_DATE, sqlDateTimes.localDate(resultSet, "ship_date"));
        assertEquals(LOCAL_TIME, sqlDateTimes.localTime(resultSet, "opens_at"));
        assertEquals(OFFSET_TIME, sqlDateTimes.offsetTime(resultSet, "shift_end"));
        assertEquals(INSTANT, sqlDateTimes.instant(resultSet, "occurred_at"));
        assertEquals(ZONED_DATE_TIME, sqlDateTimes.zonedDateTime(resultSet, "zoned_at"));
    }

    @Test
    void fallbackReadsSqlTypesFromResultSet() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getTimestamp("paid_at")).thenReturn(Timestamp.from(INSTANT));
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LOCAL_DATE_TIME));
        when(resultSet.getDate("ship_date")).thenReturn(java.sql.Date.valueOf(LOCAL_DATE));
        when(resultSet.getTime("opens_at")).thenReturn(Time.valueOf(LOCAL_TIME));
        when(resultSet.getTime("shift_end")).thenReturn(Time.valueOf(LOCAL_TIME));
        when(resultSet.getTimestamp("occurred_at")).thenReturn(Timestamp.from(INSTANT));
        when(resultSet.getTimestamp("zoned_at")).thenReturn(Timestamp.from(INSTANT));

        SqlDateTimes sqlDateTimes = new SqlDateTimes("Oracle");

        assertEquals(
                OFFSET_DATE_TIME.withOffsetSameInstant(ZoneOffset.UTC),
                sqlDateTimes.offsetDateTime(resultSet, "paid_at"));
        assertEquals(LOCAL_DATE_TIME, sqlDateTimes.localDateTime(resultSet, "created_at"));
        assertEquals(LOCAL_DATE, sqlDateTimes.localDate(resultSet, "ship_date"));
        assertEquals(LOCAL_TIME, sqlDateTimes.localTime(resultSet, "opens_at"));
        assertEquals(OffsetTime.of(LOCAL_TIME, ZoneOffset.UTC), sqlDateTimes.offsetTime(resultSet, "shift_end"));
        assertEquals(INSTANT, sqlDateTimes.instant(resultSet, "occurred_at"));
        assertEquals(
                ZONED_DATE_TIME.withZoneSameInstant(ZoneOffset.UTC),
                sqlDateTimes.zonedDateTime(resultSet, "zoned_at"));
    }
}
