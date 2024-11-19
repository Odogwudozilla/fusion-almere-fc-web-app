package com.fusionalmerefc.service;

import java.util.List;
import java.util.Optional;

import com.fusionalmerefc.config.ServiceResult;

public interface BaseService<T, ID> {
    ServiceResult<Optional<T>> findById(ID id);
    ServiceResult<List<T>> findAll();
    ServiceResult<T> save(T entity);
    ServiceResult<Void> deleteById(ID id);
}
