package com.fusionalmerefc.service;

import java.util.UUID;

import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.RolePermission;

public interface RolePermissionService extends BaseService<RolePermission, UUID> {
    ServiceResult<RolePermission> deleteByUuid(UUID roleUuid);
}
