package com.fusionalmerefc.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.RolePermission;
import com.fusionalmerefc.repository.RolePermissionRepository;
import com.fusionalmerefc.service.RolePermissionService;

@Service
public class RolePermissionServiceImpl extends BaseServiceImpl<RolePermission, UUID> implements RolePermissionService {
    private RolePermissionRepository rolePermissionRepository;

    public RolePermissionServiceImpl(RolePermissionRepository rolePermissionRepository) {
        super(rolePermissionRepository);
        this.rolePermissionRepository = rolePermissionRepository;
    }

    public ServiceResult<RolePermission> deleteByUuid(UUID roleUuid) {
        ServiceResult<RolePermission> result = new ServiceResult<>();
            try {
                List<RolePermission> existingRolePermissions = rolePermissionRepository.findByRoleUuid(roleUuid);
                rolePermissionRepository.deleteAll(existingRolePermissions);
                result.setSuccess(true);  // Assuming the method exists in your repository
            
            } catch (Exception e) {
                result.setSuccess(false);
                result.setApiError(new ApiError("Error deleting role permissions: " + e.getMessage(), ApiErrorSeverity.ERROR));
            }
        return result;
    }


}
