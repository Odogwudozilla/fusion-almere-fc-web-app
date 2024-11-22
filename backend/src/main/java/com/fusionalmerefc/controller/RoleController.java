package com.fusionalmerefc.controller;

import com.fusionalmerefc.DTOs.RoleDTO;
import com.fusionalmerefc.DTOs.DTOMapper;
import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Permission;
import com.fusionalmerefc.model.Role;
import com.fusionalmerefc.model.RolePermission;
import com.fusionalmerefc.service.RoleService;
import com.fusionalmerefc.service.PermissionService;
import com.fusionalmerefc.service.RolePermissionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    private final RoleService roleService;
    private final RolePermissionService rolePermissionService;
    private final DTOMapper dtoMapper;

    public RoleController(RoleService roleService, RolePermissionService rolePermissionService, PermissionService permissionService, DTOMapper dtoMapper) {
        this.roleService = roleService;
        this.rolePermissionService = rolePermissionService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping("/bulk-add")
    public ResponseEntity<?> bulkAddRoles(@RequestBody List<Role> roles) {
        ServiceResult<List<Role>> result = roleService.saveAll(roles);

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getApiError());
        }

        // You could log the bulk role creation result
        log.info("Successfully added roles: {}", result.getData());
        
        return ResponseEntity.ok(result.getData());
    }

    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        // Fetch all roles
        ServiceResult<List<Role>> result = roleService.findAll();

        // Handle errors
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.getApiError());
        }

        // Convert roles to RoleDTOs
        ServiceResult<List<RoleDTO>> dtoResult = roleService.convertRolesToRoleDTOs(result);

        return ResponseEntity.ok(dtoResult.getData());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable UUID id) {
        ServiceResult<Optional<Role>> result = roleService.findById(id);

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.getApiError());
        }

        if (result.getData().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("Role not found with ID: " + id, ApiErrorSeverity.INFO));
        }

        return ResponseEntity.ok(result.getData().get());
    }

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody RoleDTO incomingRoleDTO) {
        if (incomingRoleDTO.getName() == null || incomingRoleDTO.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("Role name must not be empty", ApiErrorSeverity.INFO));
        }

        // Convert incoming RoleDTO to Role entity
        Role incomingRole = dtoMapper.convertToRole(incomingRoleDTO);

        // Save the Role
        ServiceResult<Role> result = roleService.save(incomingRole);

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getApiError());
        }

        // Log creation of the role, especially if it's a superuser role
        if (incomingRole.getIsSuperUser()) {
            log.info("Superuser role created: {}", result.getData().getName());
        }

        // Convert saved Role entity back to RoleDTO for the response
        RoleDTO savedRoleDTO = dtoMapper.convertToRoleDTO(result.getData());

        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoleDTO);
    }

    @PutMapping("/{externalIdentifier}")
    public ResponseEntity<?> updateRole(@PathVariable String externalIdentifier, @RequestBody RoleDTO updatedRoleDTO) {
        if (updatedRoleDTO.getName() == null || updatedRoleDTO.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("Role name must not be empty", ApiErrorSeverity.INFO));
        }

        // Find the existing Role entity by externalIdentifier
        ServiceResult<Optional<Role>> existingRoleResult = roleService.findByExternalIdentifier(externalIdentifier);

        if (!existingRoleResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(existingRoleResult.getApiError());
        }

        if (existingRoleResult.getData().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("Role not found with External Identifier: " + externalIdentifier, ApiErrorSeverity.INFO));
        }

        Role existingRole = existingRoleResult.getData().get();

        // Map the incoming RoleDTO to the existing Role entity for update
        dtoMapper.updateRoleFromDTO(existingRole, updatedRoleDTO);

        List<Permission> assignedPermissions = roleService.findPermissionsByExternalIdentifier(updatedRoleDTO.getAssignedPermissions());
        List<RolePermission> toBeSavedRolePermissions = dtoMapper.convertFromRoleDTO(existingRole, updatedRoleDTO, assignedPermissions);

        // Save the updated Role entity
        ServiceResult<Role> saveResult = roleService.save(existingRole);
        roleService.saveOrUpdateRolePermissions(toBeSavedRolePermissions);

        if (!saveResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(saveResult.getApiError());
        }

        // Log the update for a superuser role if necessary
        if (updatedRoleDTO.getIsSuperUser()) {
            log.info("Updated role to superuser: {}", saveResult.getData().getName());
        }

        // Convert the updated Role entity back to RoleDTO for the response
        RoleDTO updatedRoleDTOResponse = dtoMapper.convertToRoleDTO(saveResult.getData());

        return ResponseEntity.ok(updatedRoleDTOResponse);
    }

    @DeleteMapping("/{externalIdentifier}")
    public ResponseEntity<?> deleteRole(@PathVariable String externalIdentifier) {
        // Find the existing Role entity by externalIdentifier
        ServiceResult<Optional<Role>> existingRoleResult = roleService.findByExternalIdentifier(externalIdentifier);

        if (!existingRoleResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(existingRoleResult.getApiError());
        }

        if (existingRoleResult.getData().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("Role not found with External Identifier: " + externalIdentifier, ApiErrorSeverity.INFO));
        }

        Role existingRole = existingRoleResult.getData().get();

        ServiceResult<Void> roleDeleteResult = roleService.deleteById(existingRole.getUuid());
        if (!roleDeleteResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(roleDeleteResult.getApiError());
        }

        ServiceResult<Void> rolePermissionDeleteResult = rolePermissionService.deleteById(existingRole.getUuid());
        if (!rolePermissionDeleteResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rolePermissionDeleteResult.getApiError());
        }

        return ResponseEntity.noContent().build();
    }

    
}
