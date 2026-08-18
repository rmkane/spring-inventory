package org.acme.inventory.dto.page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PageQueryTest {

    @Test
    void nullAndOutOfRangeValuesUseDefaults() {
        PageQuery query = new PageQuery(null, 500, "  ", "nope");
        assertEquals(1, query.page());
        assertEquals(100, query.size());
        assertNull(query.sort());
        assertEquals("asc", query.dir());
    }

    @Test
    void offsetIsZeroBased() {
        assertEquals(20, new PageQuery(3, 10, "name", "asc").offset());
    }

    @Test
    void clampToMovesPastTheLastPage() {
        PageQuery query = new PageQuery(9, 10, "name", "asc").clampTo(25);
        assertEquals(3, query.page());
        assertEquals(10, query.size());
    }

    @Test
    void toggleDirReversesTheActiveColumn() {
        PageQuery query = new PageQuery(1, 10, "name", "asc");
        assertEquals("desc", query.toggleDir("name"));
        assertEquals("asc", query.toggleDir("price"));
    }

    @Test
    void withDefaultsFillsBlankSort() {
        PageQuery query = PageQuery.defaults().withDefaults("createdAt", "desc");
        assertEquals("createdAt", query.sort());
        assertEquals("desc", query.dir());
        assertFalse(query.ascending());
        assertTrue(query.sortedBy("createdAt"));
    }
}
