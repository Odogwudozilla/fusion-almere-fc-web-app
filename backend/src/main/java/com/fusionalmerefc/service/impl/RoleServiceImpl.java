package com.fusionalmerefc.service.impl;

import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Permission;
import com.fusionalmerefc.model.Role;
import com.fusionalmerefc.model.RolePermission;
import com.fusionalmerefc.repository.PermissionRepository;
import com.fusionalmerefc.repository.RolePermissionRepository;
import com.fusionalmerefc.repository.RoleRepository;
import com.fusionalmerefc.service.RoleService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl extends BaseServiceImpl<Role, UUID> implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, 
                           PermissionRepository permissionRepository,
                           RolePermissionRepository rolePermissionRepository) {
        super(roleRepository);
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    @Transactional
    public ServiceResult<Role> save(Role role) {
        ServiceResult<Role> result = new ServiceResult<>();

        try {
            // Save the Role itself first
            Role savedRole = roleRepository.save(role);

            // If the role is a superuser, assign all permissions to this role
            if (role.getIsSuperUser()) {
                assignAllPermissionsToSuperUserRole(savedRole);
            }

            result.setData(savedRole);
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setApiError(new ApiError("Failed to save role: " + ex.getMessage(), ApiErrorSeverity.ERROR));
        }

        return result;
    }

    @Override
    @Transactional
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

            // Save roles (same behavior as save for single role)
            for (Role role : roles) {
                Role savedRole = roleRepository.save(role);
                if (role.getIsSuperUser()) {
                    assignAllPermissionsToSuperUserRole(savedRole);
                }
            }
            result.setData(roles);
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setApiError(new ApiError("Failed to save roles: " + ex.getMessage(), ApiErrorSeverity.ERROR));
        }

        return result;
    }

    // Helper method to assign all permissions to a superuser role
    private void assignAllPermissionsToSuperUserRole(Role role) {
        List<Permission> allPermissions = permissionRepository.findAll();
        for (Permission permission : allPermissions) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole(role);
            rolePermission.setPermission(permission);
            rolePermission.setExternalIdentifier(generateExternalIdentifier(role, permission));
            rolePermissionRepository.save(rolePermission); // Save the new RolePermission entry
        }
    }

    // Generate external identifier for RolePermission (you can customize this if necessary)
    private String generateExternalIdentifier(Role role, Permission permission) {
        // This method could be customized to generate a unique identifier for each RolePermission
        return role.getExternalIdentifier() + ":" + permission.getExternalIdentifier();
    }
}

