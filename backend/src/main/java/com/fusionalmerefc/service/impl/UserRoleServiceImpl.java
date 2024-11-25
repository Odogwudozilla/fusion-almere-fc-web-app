package com.fusionalmerefc.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.UserRole;
import com.fusionalmerefc.repository.UserRoleRepository;
import com.fusionalmerefc.service.UserRoleService;

@Service
public class UserRoleServiceImpl extends BaseServiceImpl<UserRole, UUID> implements UserRoleService {
    private UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        super(userRoleRepository);
        this.userRoleRepository = userRoleRepository;
    }

    public ServiceResult<UserRole> deleteByUuid(UUID userUuid) {
        ServiceResult<UserRole> result = new ServiceResult<>();
            try {
                List<UserRole> existingUserRoles = userRoleRepository.findByUserUuid(userUuid);
                userRoleRepository.deleteAll(existingUserRoles);
                result.setSuccess(true); 
            
            } catch (Exception e) {
                result.setSuccess(false);
                result.setApiError(new ApiError("Error deleting user roles: " + e.getMessage(), ApiErrorSeverity.ERROR));
            }
        return result;
    }


}
