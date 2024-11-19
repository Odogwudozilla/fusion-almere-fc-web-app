package com.fusionalmerefc.service.impl;

import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.repository.BaseRepository;
import com.fusionalmerefc.service.BaseService;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;

public abstract class BaseServiceImpl<T, ID> implements BaseService<T, ID> {

    protected final BaseRepository<T, ID> repository;

    public BaseServiceImpl(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    
    @Override
    public ServiceResult<Optional<T>> findById(ID id) {
        ServiceResult<Optional<T>> result = new ServiceResult<>();
        try {
            Optional<T> entity = repository.findById(id);
            result.setData(entity);
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setApiError(new ApiError("Error finding entity: " + ex.getMessage(), ApiErrorSeverity.ERROR));
        }
        return result;
    }

    @Override
    public ServiceResult<List<T>> findAll() {
        ServiceResult<List<T>> result = new ServiceResult<>();
        try {
            List<T> entities = repository.findAll();
            result.setData(entities);
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setApiError(new ApiError("Error retrieving entities: " + ex.getMessage(), ApiErrorSeverity.ERROR));
        }
        return result;
    }

    @Override
    public ServiceResult<T> save(T entity) {
        ServiceResult<T> result = new ServiceResult<>();
        try {
            T savedEntity = repository.save(entity);
            result.setData(savedEntity);
        } catch (DataAccessException ex) {
            result.setSuccess(false);
            result.setApiError(new ApiError("Database error: " + ex.getMessage(), ApiErrorSeverity.ERROR));
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setApiError(new ApiError("Error saving entity: " + ex.getMessage(), ApiErrorSeverity.ERROR));
        }
        return result;
    }

    @Override
    public ServiceResult<Void> deleteById(ID id) {
        ServiceResult<Void> result = new ServiceResult<>();
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setApiError(new ApiError("Error deleting entity: " + ex.getMessage(), ApiErrorSeverity.ERROR));
        }
        return result;
    }
    
}
