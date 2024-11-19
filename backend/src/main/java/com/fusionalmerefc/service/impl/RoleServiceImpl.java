package com.fusionalmerefc.service.impl;

import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Role;
import com.fusionalmerefc.repository.RoleRepository;
import com.fusionalmerefc.service.RoleService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends BaseServiceImpl<Role, UUID> implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        super(roleRepository); 
        this.roleRepository = roleRepository;
    }

    @Override
    public ServiceResult<List<Role>> saveAll(List<Role> roles) {
        ServiceResult<List<Role>> result = new ServiceResult<>();
        
        Set<String> seenIdentifiers = new HashSet<>();
        for (Role role : roles) {
            if (role.getExternalIdentifier() == null || role.getName() == null) {
                throw new IllegalArgumentException("Name and External Identifier cannot be null.");
            }
            if (!seenIdentifiers.add(role.getExternalIdentifier())) {
                throw new IllegalArgumentException("Duplicate External Identifier found: " + role.getExternalIdentifier());
            }
        }
        try {
            if (roles == null || roles.isEmpty()) {
                throw new IllegalArgumentException("Role list cannot be null or empty.");
            }
            List<Role> savedRoles = roleRepository.saveAll(roles);
            result.setData(savedRoles);
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setApiError(new ApiError("Failed to save roles: " + ex.getMessage(), ApiErrorSeverity.ERROR));
        }
        
        return result;
    }

    public Role findByName(String name) {
        return roleRepository.findByNameIgnoreCase(name);
    }

    
}
