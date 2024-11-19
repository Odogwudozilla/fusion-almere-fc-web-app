package com.fusionalmerefc.service;

import java.util.List;
import java.util.UUID;

import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Role;

public interface RoleService extends BaseService<Role, UUID> {
    ServiceResult<List<Role>> saveAll(List<Role> roles);
}
