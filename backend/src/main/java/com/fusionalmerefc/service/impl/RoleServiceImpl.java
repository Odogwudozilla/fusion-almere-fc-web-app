package com.fusionalmerefc.service.impl;

import com.fusionalmerefc.DTOs.PermissionDTO;
import com.fusionalmerefc.DTOs.RoleDTO;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    // Generate external identifier for RolePermission
    public static String generateExternalIdentifier(Role role, Permission permission) {
        // This method could be customized to generate a unique identifier for each RolePermission
        return role.getExternalIdentifier() + ":" + permission.getExternalIdentifier();
    }

    @Override
    public ServiceResult<List<RoleDTO>> convertRolesToRoleDTOs(ServiceResult<List<Role>> serviceResultRoles) {
        ServiceResult<List<RoleDTO>> resultDTO = new ServiceResult<>();

        List<Role> roles = serviceResultRoles.getData();

        resultDTO.setData(roles.stream().map(this::convertToRoleDTO).collect(Collectors.toList()));
        resultDTO.setSuccess(serviceResultRoles.isSuccess());
        resultDTO.setApiError(serviceResultRoles.getApiError());
    
        return resultDTO;
    }

    private RoleDTO convertToRoleDTO(Role role) {
        // Retrieve all RolePermission entries associated with this role
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleUuid(role.getUuid());
        
        // Map each RolePermission to its corresponding Permission and convert to PermissionDTO
        List<PermissionDTO> assignedPermissions = rolePermissions
        .stream()
            .map(rolePermission -> {
                Permission permission = rolePermission.getPermission(); // Fetch the Permission entity

                return new PermissionDTO(
                    permission.getExternalIdentifier(), 
                    permission.getName(),
                    permission.getDescription(),
                    permission.isForSuperUserOnly()
                ); // Convert to DTO
            })
            .collect(Collectors.toList());

        // Construct the RoleDTO
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setExternalIdentifier(role.getExternalIdentifier());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());
        roleDTO.setIsSuperUser(role.getIsSuperUser());
        roleDTO.setAssignedPermissions(assignedPermissions);
        return roleDTO;
    } 

    @Override
    public List<Permission> findPermissionsByExternalIdentifier(List<PermissionDTO> permissionDTOs) {
        List<Permission> permissions = new ArrayList<>();

        for (PermissionDTO permissionDTO : permissionDTOs) {
            Optional<Permission> optionalPermission = permissionRepository.findByExternalIdentifier(permissionDTO.getExternalIdentifier());
            if (optionalPermission.isPresent()) {
                permissions.add(optionalPermission.get());
            }
        }

        return permissions;
    }
    
    @Override
    public void saveOrUpdateRolePermissions(List<RolePermission> toBeSavedRolePermissions) {
        if (toBeSavedRolePermissions.isEmpty()) {
            return; // Nothing to save or update
        }
    
        // Retrieve existing role permissions for the given role
        UUID roleUuid = toBeSavedRolePermissions.get(0).getRole().getUuid();
        List<RolePermission> existingRolePermissions = rolePermissionRepository.findByRoleUuid(roleUuid);
    
        // Convert lists to sets for more efficient operations
        Set<RolePermission> toBeSavedSet = new HashSet<>(toBeSavedRolePermissions);
        Set<RolePermission> existingSet = new HashSet<>(existingRolePermissions);
    
        // Determine permissions to delete
        Set<RolePermission> toDelete = new HashSet<>(existingSet);
        toDelete.removeAll(toBeSavedSet); // Entries in existing but not in toBeSaved
    
        // Determine permissions to save
        Set<RolePermission> toSave = new HashSet<>(toBeSavedSet);
        toSave.removeAll(existingSet); // Entries in toBeSaved but not in existing
    
        // Perform delete and save operations
        if (!toDelete.isEmpty()) {
            rolePermissionRepository.deleteAll(toDelete);
        }
        if (!toSave.isEmpty()) {
            rolePermissionRepository.saveAll(toSave);
        }
    }
    
}

