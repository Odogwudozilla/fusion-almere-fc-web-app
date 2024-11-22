package com.fusionalmerefc.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fusionalmerefc.DTOs.PermissionDTO;
import com.fusionalmerefc.DTOs.RoleDTO;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Permission;
import com.fusionalmerefc.model.Role;
import com.fusionalmerefc.model.RolePermission;

public interface RoleService extends BaseService<Role, UUID> {
    ServiceResult<List<Role>> saveAll(List<Role> roles);
    ServiceResult<List<RoleDTO>> convertRolesToRoleDTOs(ServiceResult<List<Role>> serviceResultRoles);
    ServiceResult<Optional<Role>> findByExternalIdentifier(String externalIdentifier);
    List<Permission> findPermissionsByExternalIdentifier(List<PermissionDTO> permissionDTOs);
    void saveOrUpdateRolePermissions(List<RolePermission> toBeSavedRolePermissions);
}
