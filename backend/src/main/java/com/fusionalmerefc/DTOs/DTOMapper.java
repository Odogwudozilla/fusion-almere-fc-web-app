package com.fusionalmerefc.DTOs;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fusionalmerefc.Utils.UtilityFunctions;
import com.fusionalmerefc.model.*;
import com.fusionalmerefc.model.constants.MembershipType;
import com.fusionalmerefc.model.constants.StatusType;
import com.fusionalmerefc.service.impl.RoleServiceImpl;

/**
 * A utility class for mapping between DTOs and entities.
 */
@Component
public class DTOMapper {

    // --- Role Conversion Methods ---

    /**
     * Converts a RoleDTO to a Role entity.
     *
     * @param roleDTO the RoleDTO to convert.
     * @return the corresponding Role entity, or null if the input is null.
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
     *
     * @param role the Role entity to convert.
     * @return the corresponding RoleDTO, or null if the input is null.
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
     * Updates an existing Role entity with data from a RoleDTO.
     *
     * @param existingRole   the Role entity to update.
     * @param updatedRoleDTO the RoleDTO containing the updated data.
     */
    public void updateRoleFromDTO(Role existingRole, RoleDTO updatedRoleDTO) {
        if (existingRole == null || updatedRoleDTO == null) {
            return;
        }
        mapCommonRoleFields(updatedRoleDTO, existingRole);
    }

    /**
     * Converts a RoleDTO and a list of Permissions into RolePermission entities.
     *
     * @param role        the Role entity associated with the permissions.
     * @param roleDTO     the RoleDTO containing the permissions.
     * @param permissions the list of Permission entities.
     * @return a list of RolePermission entities.
     */
    public List<RolePermission> convertFromRoleDTO(Role role, RoleDTO roleDTO, List<Permission> permissions) {
        if (role == null || roleDTO == null || permissions == null) {
            return Collections.emptyList();
        }
        return permissions.stream()
                .map(permission -> createRolePermission(role, permission))
                .collect(Collectors.toList());
    }

    public List<RoleDTO> mapRolesToRoleDTO(List<Role> assignedRoles) {
        return assignedRoles.stream()
            .map(role -> convertToRoleDTO(role))
            .collect(Collectors.toList());
    }

    // --- User Conversion Methods ---

    /**
     * Converts a UserDTO to a User entity.
     *
     * @param userDTO the UserDTO to convert.
     * @return the corresponding User entity, or null if the input is null.
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
        user.setIdentifierForUrl(UtilityFunctions.generateUniqueIdentifier(userDTO.getUsername(), "FusionUser"));
        user.setActivatedAt(userDTO.getActivatedAt() == null ? LocalDateTime.now() : userDTO.getActivatedAt());
        user.setMembershipType(matchToEnum(userDTO.getMembershipType(), MembershipType.class));
        user.setStatus(matchToEnum(userDTO.getStatus(), StatusType.class));
        return user;
    }

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user the User entity to convert.
     * @return the corresponding UserDTO, or null if the input is null.
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
        userDTO.setActivatedAt(user.getActivatedAt());
        userDTO.setStatus(user.getStatus().toString());
        return userDTO;
    }

    /**
     * Converts a UserDTO and a list of Roles into UserRole entities.
     *
     * @param user     the User entity associated with the roles.
     * @param userDTO  the UserDTO containing the roles.
     * @param roles    the list of Role entities.
     * @return a list of UserRole entities.
     */
    public List<UserRole> convertFromUserDTO(User user, UserDTO userDTO, List<Role> roles) {
        if (user == null || userDTO == null || roles == null) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(role -> createUserRole(user, role))
                .collect(Collectors.toList());
    }

    // --- Permission Conversion Methods ---

    /**
     * Converts a PermissionDTO to a Permission entity.
     *
     * @param incomingPermissionDTO the PermissionDTO to convert.
     * @return the corresponding Permission entity.
     */
    public Permission convertToPermission(PermissionDTO incomingPermissionDTO) {
        if (incomingPermissionDTO == null) {
            return null;
        }
        Permission permission = new Permission();
        permission.setExternalIdentifier(incomingPermissionDTO.getExternalIdentifier());
        permission.setName(incomingPermissionDTO.getName());
        permission.setDescription(incomingPermissionDTO.getDescription());
        permission.setForSuperUserOnly(incomingPermissionDTO.getIsForSuperUserOnly());
        return permission;
    }

    // --- Helper Methods ---

    /**
     * Maps common fields between Role and RoleDTO.
     *
     * @param source the source object (Role or RoleDTO).
     * @param target the target object (Role or RoleDTO).
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
     *
     * @param role       the Role entity.
     * @param permission the Permission entity.
     * @return a RolePermission entity.
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
     *
     * @param user the User entity.
     * @param role the Role entity.
     * @return a UserRole entity.
     */
    private UserRole createUserRole(User user, Role role) {
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setExternalIdentifier(UtilityFunctions.generateCombinedExternalIdentifier(user, role));
        return userRole;
    }

    /**
     * Matches a string value to an enum constant.
     *
     * @param value    the string value.
     * @param enumType the enum class.
     * @param <T>      the enum type.
     * @return the matching enum constant.
     * @throws IllegalArgumentException if no match is found.
     */
    public <T extends Enum<T>> T matchToEnum(String value, Class<T> enumType) {
        for (T constant : enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Invalid value for enum " + enumType.getSimpleName() + ": " + value);
    }
}
