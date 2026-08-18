package org.acme.inventory.dto.page;

import io.swagger.v3.oas.annotations.media.Schema;

// spotless:off
@Schema(name = "PageQuery", description = "List paging and sort controls")
public record PageQuery(
        @Schema(description = "1-based page number", example = "1", defaultValue = "1")
        Integer page,

        @Schema(description = "Page size", example = "10", defaultValue = "10")
        Integer size,

        @Schema(description = "Sort field name", example = "name")
        String sort,

        @Schema(description = "Sort direction", example = "asc", allowableValues = { "asc", "desc" })
        String dir) {
    // spotless:on

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_SIZE = 100;

    public PageQuery {
        page = page == null || page < 1 ? DEFAULT_PAGE : page;
        size = size == null || size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        sort = blankToNull(sort);
        dir = normalizeDir(dir);
    }

    public static PageQuery defaults() {
        return new PageQuery(DEFAULT_PAGE, DEFAULT_SIZE, null, null);
    }

    public int offset() {
        return (page - 1) * size;
    }

    public boolean ascending() {
        return !"desc".equalsIgnoreCase(dir);
    }

    public boolean sortedBy(String sortKey) {
        return sortKey != null && sortKey.equalsIgnoreCase(sort);
    }

    public String toggleDir(String sortKey) {
        if (sortedBy(sortKey) && ascending()) {
            return "desc";
        }
        return "asc";
    }

    public PageQuery withDefaults(String defaultSort, String defaultDir) {
        return new PageQuery(page, size, sort == null ? defaultSort : sort, dir == null ? defaultDir : dir);
    }

    public PageQuery clampTo(long totalElements) {
        int totalPages = totalPages(size, totalElements);
        if (totalPages <= 0 || page <= totalPages) {
            return this;
        }
        return new PageQuery(totalPages, size, sort, dir);
    }

    public static int totalPages(int size, long totalElements) {
        if (size <= 0 || totalElements <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElements / size);
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private static String normalizeDir(String value) {
        String dir = blankToNull(value);
        if (dir == null) {
            return null;
        }
        return "desc".equalsIgnoreCase(dir) ? "desc" : "asc";
    }
}
