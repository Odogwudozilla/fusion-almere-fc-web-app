package com.fusionalmerefc.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Permission;

public interface PermissionService extends BaseService<Permission, UUID> {
    ServiceResult<List<Permission>> saveAll(List<Permission> permissions);
    ServiceResult<Optional<Permission>> findByExternalIdentifier(String externalIdentifier);
}
