package org.acme.inventory.dto.page;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        PageQuery query,
        long totalElements) {

    public PageResult {
        content = List.copyOf(content);
    }

    public int page() {
        return query.page().intValue();
    }

    public int size() {
        return query.size().intValue();
    }

    public int totalPages() {
        return PageQuery.totalPages(size(), totalElements);
    }

    public boolean hasPrevious() {
        return page() > 1;
    }

    public boolean hasNext() {
        return page() < totalPages();
    }

    public boolean isFirst() {
        return page() <= 1;
    }

    public boolean isLast() {
        return totalPages() == 0 || page() >= totalPages();
    }

    public int lastPage() {
        return Math.max(1, totalPages());
    }

    public long fromIndex() {
        if (totalElements == 0) {
            return 0;
        }
        return (long) (page() - 1) * size() + 1;
    }

    public long toIndex() {
        if (totalElements == 0) {
            return 0;
        }
        return Math.min((long) page() * size(), totalElements);
    }
}
