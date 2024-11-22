package com.fusionalmerefc.DTOs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fusionalmerefc.model.Permission;
import com.fusionalmerefc.model.Role;
import com.fusionalmerefc.model.RolePermission;
import com.fusionalmerefc.service.impl.RoleServiceImpl;

@Component 
public class DTOMapper {
    public Role convertToRole(RoleDTO roleDTO) {
        Role role = new Role();
        role.setExternalIdentifier(roleDTO.getExternalIdentifier());
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        role.setIsSuperUser(roleDTO.getIsSuperUser());
        return role;
    }

    public RoleDTO convertToRoleDTO(Role role) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setExternalIdentifier(role.getExternalIdentifier());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());
        roleDTO.setIsSuperUser(role.getIsSuperUser());
        return roleDTO;
    }

    public void updateRoleFromDTO(Role existingRole, RoleDTO updatedRoleDTO) {
        existingRole.setName(updatedRoleDTO.getName());
        existingRole.setDescription(updatedRoleDTO.getDescription());
        existingRole.setIsSuperUser(updatedRoleDTO.getIsSuperUser());
        existingRole.setExternalIdentifier(updatedRoleDTO.getExternalIdentifier());
    }

    public List<RolePermission> convertFromRoleDTO(Role role, RoleDTO roleDTO, List<Permission> permissions) {
        List<RolePermission> rolePermissions = new ArrayList<>();
        
        for (Permission permission : permissions) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole(role);
            rolePermission.setPermission(permission);
            rolePermission.setExternalIdentifier(RoleServiceImpl.generateExternalIdentifier(role, permission));
            rolePermissions.add(rolePermission);
        }
        
        return rolePermissions;
    }

}
