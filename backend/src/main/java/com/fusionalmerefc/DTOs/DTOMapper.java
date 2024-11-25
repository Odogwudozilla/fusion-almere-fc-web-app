package com.fusionalmerefc.DTOs;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

import com.fusionalmerefc.model.Permission;
import com.fusionalmerefc.model.Role;
import com.fusionalmerefc.model.RolePermission;
import com.fusionalmerefc.model.User;
import com.fusionalmerefc.model.UserRole;
import com.fusionalmerefc.model.constants.MembershipType;
import com.fusionalmerefc.model.constants.StatusType;
import com.fusionalmerefc.service.impl.RoleServiceImpl;
import com.fusionalmerefc.service.impl.UserServiceImpl;

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

    /**
     * Converts a UserDTO and a list of Roles into UserRole entities.
     * @param user The User entity associated with the permissions.
     * @param roleDTO The UserDTO containing the roles.
     * @param roles The list of Role entities.
     * @return A list of UserRoles entities.
     */
    public List<UserRole> convertFromUserDTO(User user, UserDTO userDTO, List<Role> roles) {
        if (user == null || userDTO == null || roles == null) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(role -> createUserRole(user, role))
                .collect(Collectors.toList());
    }

    // --- User Conversion Methods ---

    /**
     * Converts a UserDTO to a User entity.
     * @param userDTO The UserDTO to convert.
     * @return The corresponding User entity.
     */
    public User mapToUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setExternalIdentifier(userDTO.getExternalIdentifier());
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setMobileNumber(userDTO.getMobileNumber());
        user.setWhatsappNumber(userDTO.getWhatsappNumber());
        user.setPostcode(userDTO.getPostcode());
        user.setAddress(userDTO.getAddress());
        user.setProfilePictureUrl(userDTO.getProfilePictureUrl());
        user.setMembershipType(matchToEnum(userDTO.getMembershipType(), MembershipType.class));
        user.setActivatedAt(LocalDateTime.now());
        user.setStatus(matchToEnum(userDTO.getStatus(), StatusType.class));

        return user;
    }

    /**
     * Converts a UserDTO to a User entity.
     * @param user The User to convert.
     * @return The corresponding UserDTO entity.
     */
    public UserDTO mapToUserDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setExternalIdentifier(user.getExternalIdentifier());
        userDTO.setName(user.getName());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setMobileNumber(user.getMobileNumber());
        userDTO.setWhatsappNumber(user.getWhatsappNumber());
        userDTO.setPostcode(user.getPostcode());
        userDTO.setAddress(user.getAddress());
        userDTO.setProfilePictureUrl(user.getProfilePictureUrl());
        userDTO.setMembershipType(user.getMembershipType().toString());
        userDTO.setActivatedAt(LocalDateTime.now());
        userDTO.setStatus(user.getStatus().toString());

        return userDTO;
    }

    /**
     * Helper method to convert a list of RoleDTOs to Role entities.
     * @param roleDTOs The list of RoleDTOs.
     * @return A list of Role entities.
     */
    private List<Role> convertToRoles(List<RoleDTO> roleDTOs) {
        if (roleDTOs == null) {
            return Collections.emptyList();
        }
        return roleDTOs.stream()
                .map(this::convertToRole)  // Converts each RoleDTO to Role
                .collect(Collectors.toList());
    }

    // --- Permission Conversion Methods ---

    public Permission convertToPermission(PermissionDTO incomingPermissionDTO) {
        Permission permission = new Permission();

        permission.setExternalIdentifier(incomingPermissionDTO.getExternalIdentifier());
        permission.setName(incomingPermissionDTO.getName());
        permission.setDescription(incomingPermissionDTO.getDescription());
        permission.setForSuperUserOnly(incomingPermissionDTO.getIsForSuperUserOnly());

        return permission;
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

    /**
     * Creates a UserRole entity.
     * @param user The User entity.
     * @param role The Role entity.
     * @return A UserRole entity.
     */
    private UserRole createUserRole(User user, Role role) {
        UserRole userrole = new UserRole();
        userrole.setUser(user);
        userrole.setRole(role);
        userrole.setExternalIdentifier(UserServiceImpl.generateExternalIdentifier(user, role));
        return userrole;
    }

    public <T extends Enum<T>> T matchToEnum(String value, Class<T> enumType) {
        for (T constant : enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Invalid value for enum " + enumType.getSimpleName() + ": " + value);
    }

    
}
