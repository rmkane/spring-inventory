package org.acme.inventory.repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.experimental.UtilityClass;

import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;

@UtilityClass
public final class SqlPaging {

    public static <T> PageResult<T> fetch(
            NamedParameterJdbcTemplate jdbcTemplate,
            String countSql,
            String selectSql,
            PageQuery query,
            Map<String, String> columns,
            String defaultSort,
            String defaultDir,
            RowMapper<T> mapper) {
        long total = count(jdbcTemplate, countSql);
        PageQuery effective = resolve(query, columns, defaultSort, defaultDir).clampTo(total);
        if (total == 0) {
            return new PageResult<>(List.of(), effective, total);
        }
        List<T> content = jdbcTemplate.query(
                selectSql + orderByLimit(effective, columns),
                params(effective),
                mapper);
        return new PageResult<>(content, effective, total);
    }

    public static long count(NamedParameterJdbcTemplate jdbcTemplate, String countSql) {
        Long total = jdbcTemplate.getJdbcTemplate().queryForObject(countSql, Long.class);
        return total == null ? 0 : total;
    }

    public static PageQuery resolve(
            PageQuery query,
            Map<String, String> columns,
            String defaultSort,
            String defaultDir) {
        PageQuery withDefaults = query.withDefaults(defaultSort, defaultDir);
        if (sqlColumn(withDefaults.sort(), columns) == null) {
            return new PageQuery(withDefaults.page(), withDefaults.size(), defaultSort, withDefaults.dir());
        }
        return new PageQuery(
                withDefaults.page(),
                withDefaults.size(),
                canonicalSort(withDefaults.sort(), columns),
                withDefaults.ascending() ? "asc" : "desc");
    }

    public static String orderByLimit(PageQuery query, Map<String, String> columns) {
        String column = sqlColumn(query.sort(), columns);
        if (column == null) {
            throw new IllegalArgumentException("Unknown sort field: " + query.sort());
        }
        String direction = query.ascending() ? "ASC" : "DESC";
        return " ORDER BY " + column + " " + direction + " LIMIT :limit OFFSET :offset";
    }

    public static MapSqlParameterSource params(PageQuery query) {
        return new MapSqlParameterSource()
                .addValue("limit", query.size())
                .addValue("offset", query.offset());
    }

    private static String sqlColumn(String sort, Map<String, String> columns) {
        if (sort == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(sort)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static String canonicalSort(String sort, Map<String, String> columns) {
        for (String key : columns.keySet()) {
            if (key.equalsIgnoreCase(sort)) {
                return key;
            }
        }
        return sort;
    }
}
