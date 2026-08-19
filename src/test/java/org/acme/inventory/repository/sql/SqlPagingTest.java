package org.acme.inventory.repository.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import org.acme.inventory.dto.page.PageQuery;

class SqlPagingTest {

    private static final Map<String, String> COLUMNS = Map.of(
            "name", "p.name",
            "createdAt", "p.created_at");

    @Test
    void resolveFallsBackToDefaultSort() {
        PageQuery query = SqlPaging.resolve(new PageQuery(1, 10, "nope", "desc"), COLUMNS, "name", "asc");
        assertEquals("name", query.sort());
        assertEquals("desc", query.dir());
    }

    @Test
    void resolveCanonicalizesSortField() {
        PageQuery query = SqlPaging.resolve(new PageQuery(2, 15, "CREATEDAT", "DESC"), COLUMNS, "name", "asc");
        assertEquals("createdAt", query.sort());
        assertEquals("desc", query.dir());
        assertEquals(15, query.size());
    }

    @Test
    void orderByLimitUsesMappedColumn() {
        PageQuery query = new PageQuery(3, 10, "name", "desc");
        assertEquals(
                " ORDER BY p.name DESC LIMIT :limit OFFSET :offset",
                SqlPaging.orderByLimit(query, COLUMNS));
        assertEquals(10, SqlPaging.params(query).getValue("limit"));
        assertEquals(20, SqlPaging.params(query).getValue("offset"));
    }

    @Test
    void orderByLimitRejectsUnknownSort() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SqlPaging.orderByLimit(new PageQuery(1, 10, "nope", "asc"), COLUMNS));
    }
}
