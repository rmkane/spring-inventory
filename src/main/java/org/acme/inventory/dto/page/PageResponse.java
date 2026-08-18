package org.acme.inventory.dto.page;

import java.util.List;
import java.util.function.Function;

import io.swagger.v3.oas.annotations.media.Schema;

// spotless:off
@Schema(name = "PageResponse", description = "A page of list results")
public record PageResponse<T>(
        @Schema(description = "Items on this page")
        List<T> content,

        @Schema(description = "1-based page number", example = "1")
        int page,

        @Schema(description = "Page size", example = "10")
        int size,

        @Schema(description = "Total matching rows", example = "42")
        long totalElements,

        @Schema(description = "Total number of pages", example = "5")
        int totalPages,

        @Schema(description = "Sort field name", example = "name")
        String sort,

        @Schema(description = "Sort direction", example = "asc", allowableValues = { "asc", "desc" })
        String dir) {
    // spotless:on

    public static <S, T> PageResponse<T> from(PageResult<S> page, Function<S, T> mapper) {
        return new PageResponse<>(
                page.content().stream().map(mapper).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages(),
                page.query().sort(),
                page.query().dir());
    }
}
