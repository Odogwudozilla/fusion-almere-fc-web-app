package com.fusionalmerefc.DTOs;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fusionalmerefc.model.Permission;
import com.fusionalmerefc.model.Role;
import com.fusionalmerefc.model.RolePermission;
import com.fusionalmerefc.service.impl.RoleServiceImpl;
@Component
public class DTOMapper {

    /**
     * Converts a RoleDTO to a Role entity.
     * @param roleDTO The RoleDTO to convert.
     * @return The corresponding Role entity.
     */
    public Role convertToRole(RoleDTO roleDTO) {
        if (roleDTO == null) {
            return null;
        }
        Role role = new Role();
        mapCommonRoleFields(roleDTO, role);
        return role;
    }

    /**
     * Converts a Role entity to a RoleDTO.
     * @param role The Role entity to convert.
     * @return The corresponding RoleDTO.
     */
    public RoleDTO convertToRoleDTO(Role role) {
        if (role == null) {
            return null;
        }
        RoleDTO roleDTO = new RoleDTO();
        mapCommonRoleFields(role, roleDTO);
        return roleDTO;
    }

    /**
     * Updates an existing Role entity from a RoleDTO.
     * @param existingRole The Role entity to update.
     * @param updatedRoleDTO The RoleDTO containing the updated data.
     */
    public void updateRoleFromDTO(Role existingRole, RoleDTO updatedRoleDTO) {
        if (existingRole == null || updatedRoleDTO == null) {
            return;
        }
        mapCommonRoleFields(updatedRoleDTO, existingRole);
    }

    /**
     * Converts a RoleDTO and a list of Permissions into RolePermission entities.
     * @param role The Role entity associated with the permissions.
     * @param roleDTO The RoleDTO containing the permissions.
     * @param permissions The list of Permission entities.
     * @return A list of RolePermission entities.
     */
    public List<RolePermission> convertFromRoleDTO(Role role, RoleDTO roleDTO, List<Permission> permissions) {
        if (role == null || roleDTO == null || permissions == null) {
            return Collections.emptyList();
        }
        return permissions.stream()
                .map(permission -> createRolePermission(role, permission))
                .collect(Collectors.toList());
    }

    // --- Private helper methods ---

    /**
     * Maps common fields between Role and RoleDTO.
     * @param source The source object (Role or RoleDTO).
     * @param target The target object (Role or RoleDTO).
     */
    private void mapCommonRoleFields(Object source, Object target) {
        if (source instanceof RoleDTO && target instanceof Role) {
            RoleDTO roleDTO = (RoleDTO) source;
            Role role = (Role) target;
            role.setExternalIdentifier(roleDTO.getExternalIdentifier());
            role.setName(roleDTO.getName());
            role.setDescription(roleDTO.getDescription());
            role.setIsSuperUser(roleDTO.getIsSuperUser());
        } else if (source instanceof Role && target instanceof RoleDTO) {
            Role role = (Role) source;
            RoleDTO roleDTO = (RoleDTO) target;
            roleDTO.setExternalIdentifier(role.getExternalIdentifier());
            roleDTO.setName(role.getName());
            roleDTO.setDescription(role.getDescription());
            roleDTO.setIsSuperUser(role.getIsSuperUser());
        }
    }

    /**
     * Creates a RolePermission entity.
     * @param role The Role entity.
     * @param permission The Permission entity.
     * @return A RolePermission entity.
     */
    private RolePermission createRolePermission(Role role, Permission permission) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermission.setExternalIdentifier(RoleServiceImpl.generateExternalIdentifier(role, permission));
        return rolePermission;
    }

    public Permission convertToPermission(PermissionDTO incomingPermissionDTO) {
        Permission permission = new Permission();

        permission.setExternalIdentifier(incomingPermissionDTO.getExternalIdentifier());
        permission.setName(incomingPermissionDTO.getName());
        permission.setDescription(incomingPermissionDTO.getDescription());
        permission.setForSuperUserOnly(incomingPermissionDTO.getIsForSuperUserOnly());

        return permission;
    }
}

