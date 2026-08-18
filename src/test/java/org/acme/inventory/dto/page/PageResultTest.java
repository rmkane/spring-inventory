package org.acme.inventory.dto.page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class PageResultTest {

    @Test
    void reportsRangeAndNavigationOnAMiddlePage() {
        PageResult<String> page = new PageResult<>(
                List.of("a", "b", "c"),
                new PageQuery(2, 10, "name", "asc"),
                25);

        assertEquals(3, page.totalPages());
        assertEquals(11, page.fromIndex());
        assertEquals(20, page.toIndex());
        assertTrue(page.hasPrevious());
        assertTrue(page.hasNext());
        assertFalse(page.isFirst());
        assertFalse(page.isLast());
        assertEquals(3, page.lastPage());
    }

    @Test
    void emptyResultHasZeroRange() {
        PageResult<String> page = new PageResult<>(List.of(), PageQuery.defaults(), 0);
        assertEquals(0, page.fromIndex());
        assertEquals(0, page.toIndex());
        assertEquals(0, page.totalPages());
        assertTrue(page.isFirst());
        assertTrue(page.isLast());
        assertFalse(page.hasNext());
        assertEquals(1, page.lastPage());
    }

    @Test
    void pageResponseMapsContent() {
        PageResult<Integer> page = new PageResult<>(
                List.of(1, 2),
                new PageQuery(1, 10, "name", "asc"),
                2);
        PageResponse<String> response = PageResponse.from(page, String::valueOf);
        assertEquals(List.of("1", "2"), response.content());
        assertEquals(1, response.page());
        assertEquals(2, response.totalElements());
        assertEquals("name", response.sort());
    }
}
