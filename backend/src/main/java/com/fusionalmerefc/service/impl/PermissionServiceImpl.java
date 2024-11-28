package com.fusionalmerefc.service.impl;

import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Permission;
import com.fusionalmerefc.model.User;
import com.fusionalmerefc.repository.PermissionRepository;
import com.fusionalmerefc.service.PermissionService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl extends BaseServiceImpl<Permission, UUID> implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        super(permissionRepository); 
        this.permissionRepository = permissionRepository;
    }
    
   @Override
    public ServiceResult<List<Permission>> saveAll(List<Permission> permissions) {
        ServiceResult<List<Permission>> result = new ServiceResult<>();
        
        Set<String> seenIdentifiers = new HashSet<>();
        for (Permission permission : permissions) {
            if (permission.getExternalIdentifier() == null || permission.getName() == null) {
                throw new IllegalArgumentException("Name and External Identifier cannot be null.");
            }
            if (!seenIdentifiers.add(permission.getExternalIdentifier())) {
                throw new IllegalArgumentException("Duplicate External Identifier found: " + permission.getExternalIdentifier());
            }
        }
        try {
            if (permissions == null || permissions.isEmpty()) {
                throw new IllegalArgumentException("Permission list cannot be null or empty.");
            }
            List<Permission> savedPermission = permissionRepository.saveAll(permissions);
            result.setData(savedPermission);
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setApiError(new ApiError("Failed to save permission: " + ex.getMessage(), ApiErrorSeverity.ERROR));
        }
        
        return result;
    }

    @Override
    public ServiceResult<Page<User>> findAllWithPagination(int page, int pageSize, String sortField, String sortOrder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllWithPagination'");
    }
 

}
