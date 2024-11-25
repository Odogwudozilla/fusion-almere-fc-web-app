package com.fusionalmerefc.service;

import java.util.UUID;

import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.UserRole;

public interface UserRoleService extends BaseService<UserRole, UUID>{
    ServiceResult<UserRole> deleteByUuid(UUID userUuid);
}
