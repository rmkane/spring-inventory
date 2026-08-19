package org.acme.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;

public interface JdbcRepository<T, ID> {

    List<T> findAll();

    PageResult<T> findPage(PageQuery query);

    long count();

    Optional<T> findById(ID id);

    boolean deleteById(ID id);
}
