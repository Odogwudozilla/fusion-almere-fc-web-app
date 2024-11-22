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

    public RoleController(RoleService roleService, RolePermissionService rolePermissionService, DTOMapper dtoMapper) {
        this.roleService = roleService;
        this.rolePermissionService = rolePermissionService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping("/bulk-add")
    public ResponseEntity<?> bulkAddRoles(@RequestBody List<Role> roles) {
        ServiceResult<List<Role>> result = roleService.saveAll(roles);

        return result.isSuccess() 
            ? ResponseEntity.ok(result.getData()) 
            : buildErrorResponse(HttpStatus.BAD_REQUEST, result.getApiError());
    }

    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        ServiceResult<List<Role>> result = roleService.findAll();
        if (!result.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, result.getApiError());
        }

        ServiceResult<List<RoleDTO>> dtoResult = roleService.convertRolesToRoleDTOs(result);
        return ResponseEntity.ok(dtoResult.getData());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable UUID id) {
        ServiceResult<Optional<Role>> result = roleService.findById(id);

        if (!result.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, result.getApiError());
        }

        return result.getData().isPresent()
            ? ResponseEntity.ok(result.getData().get())
            : buildErrorResponse(HttpStatus.NOT_FOUND, new ApiError("Role not found with ID: " + id, ApiErrorSeverity.INFO));
    }

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody RoleDTO incomingRoleDTO) {
        if (isRoleNameEmpty(incomingRoleDTO)) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, new ApiError("Role name must not be empty", ApiErrorSeverity.INFO));
        }

        Role incomingRole = dtoMapper.convertToRole(incomingRoleDTO);
        List<Permission> assignedPermissions = roleService.findPermissionsByExternalIdentifier(incomingRoleDTO.getAssignedPermissions());
        List<RolePermission> toBeSavedRolePermissions = dtoMapper.convertFromRoleDTO(incomingRole, incomingRoleDTO, assignedPermissions);

        ServiceResult<Role> result = roleService.save(incomingRole);
        roleService.saveOrUpdateRolePermissions(toBeSavedRolePermissions);

        if (!result.isSuccess()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, result.getApiError());
        }

        logSuperUserRoleCreation(incomingRole, result);
        RoleDTO savedRoleDTO = dtoMapper.convertToRoleDTO(result.getData());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoleDTO);
    }

    @PutMapping("/{externalIdentifier}")
    public ResponseEntity<?> updateRole(@PathVariable String externalIdentifier, @RequestBody RoleDTO updatedRoleDTO) {
        if (isRoleNameEmpty(updatedRoleDTO)) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, new ApiError("Role name must not be empty", ApiErrorSeverity.INFO));
        }

        ServiceResult<Optional<Role>> existingRoleResult = roleService.findByExternalIdentifier(externalIdentifier);
        if (!existingRoleResult.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, existingRoleResult.getApiError());
        }

        Role existingRole = existingRoleResult.getData().orElse(null);
        if (existingRole == null) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, new ApiError("Role not found with External Identifier: " + externalIdentifier, ApiErrorSeverity.INFO));
        }

        dtoMapper.updateRoleFromDTO(existingRole, updatedRoleDTO);
        List<Permission> assignedPermissions = roleService.findPermissionsByExternalIdentifier(updatedRoleDTO.getAssignedPermissions());
        List<RolePermission> toBeSavedRolePermissions = dtoMapper.convertFromRoleDTO(existingRole, updatedRoleDTO, assignedPermissions);

        ServiceResult<Role> saveResult = roleService.save(existingRole);
        roleService.saveOrUpdateRolePermissions(toBeSavedRolePermissions);

        if (!saveResult.isSuccess()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, saveResult.getApiError());
        }

        logSuperUserRoleUpdate(updatedRoleDTO, saveResult);
        RoleDTO updatedRoleDTOResponse = dtoMapper.convertToRoleDTO(saveResult.getData());
        return ResponseEntity.ok(updatedRoleDTOResponse);
    }


    @DeleteMapping("/{externalIdentifier}")
    public ResponseEntity<?> deleteRole(@PathVariable String externalIdentifier) {
        // Fetch the existing role by externalIdentifier
        ServiceResult<Optional<Role>> existingRoleResult = roleService.findByExternalIdentifier(externalIdentifier);
        if (!existingRoleResult.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, existingRoleResult.getApiError());
        }
    
        Role existingRole = existingRoleResult.getData().orElse(null);
        if (existingRole == null) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, new ApiError("Role not found with External Identifier: " + externalIdentifier, ApiErrorSeverity.INFO));
        }
    
        // First, delete the associated role permissions before deleting the role itself
        ServiceResult<RolePermission> rolePermissionDeleteResult = rolePermissionService.deleteByUuid(existingRole.getUuid());
        if (!rolePermissionDeleteResult.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, rolePermissionDeleteResult.getApiError());
        }
    
        // Now, delete the role after removing its permissions
        ServiceResult<Void> roleDeleteResult = roleService.deleteById(existingRole.getUuid());
        if (!roleDeleteResult.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, roleDeleteResult.getApiError());
        }
    
        return ResponseEntity.noContent().build();
    }
    

    // --- Private helper methods ---

    private boolean isRoleNameEmpty(RoleDTO roleDTO) {
        return roleDTO.getName() == null || roleDTO.getName().isEmpty();
    }

    private void logSuperUserRoleCreation(Role role, ServiceResult<Role> result) {
        if (role.getIsSuperUser()) {
            log.info("Superuser role created: {}", result.getData().getName());
        }
    }

    private void logSuperUserRoleUpdate(RoleDTO updatedRoleDTO, ServiceResult<Role> saveResult) {
        if (updatedRoleDTO.getIsSuperUser()) {
            log.info("Updated role to superuser: {}", saveResult.getData().getName());
        }
    }

    private ResponseEntity<ApiError> buildErrorResponse(HttpStatus status, ApiError apiError) {
        return ResponseEntity.status(status).body(apiError);
    }
}
