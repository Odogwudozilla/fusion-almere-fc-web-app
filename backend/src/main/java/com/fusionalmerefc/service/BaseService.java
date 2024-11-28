package com.fusionalmerefc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.User;

public interface BaseService<T, ID> {
    ServiceResult<Optional<T>> findById(ID id);
    ServiceResult<List<T>> findAll();
    ServiceResult<T> save(T entity);
    ServiceResult<Void> deleteById(ID id);
    ServiceResult<Page<User>> findAllWithPagination(int page, int pageSize, String sortField, String sortOrder);
}
