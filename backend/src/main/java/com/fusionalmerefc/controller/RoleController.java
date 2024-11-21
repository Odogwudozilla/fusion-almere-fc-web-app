package com.fusionalmerefc.controller;

import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Role;
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

    public RoleController(RoleService roleService, RolePermissionService rolePermissionService, PermissionService permissionService) {
        this.roleService = roleService;
        this.rolePermissionService = rolePermissionService;
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
        ServiceResult<List<Role>> result = roleService.findAll();

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.getApiError());
        }

        return ResponseEntity.ok(result.getData());
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
    public ResponseEntity<?> createRole(@RequestBody Role incomingRole) {
        if (incomingRole.getName() == null || incomingRole.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("Role name must not be empty", ApiErrorSeverity.INFO));
        }

        // Save the Role
        ServiceResult<Role> result = roleService.save(incomingRole);

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getApiError());
        }

        // Log creation of the role, especially if it's a superuser role
        if (incomingRole.getIsSuperUser()) {
            log.info("Superuser role created: {}", result.getData().getName());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getData());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable UUID id, @RequestBody Role updatedRole) {
        if (updatedRole.getName() == null || updatedRole.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("Role name must not be empty", ApiErrorSeverity.INFO));
        }

        ServiceResult<Optional<Role>> existingRoleResult = roleService.findById(id);

        if (!existingRoleResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(existingRoleResult.getApiError());
        }

        if (existingRoleResult.getData().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("Role not found with ID: " + id, ApiErrorSeverity.INFO));
        }

        Role existingRole = existingRoleResult.getData().get();

        // Save the updated Role
        ServiceResult<Role> saveResult = roleService.save(existingRole);

        if (!saveResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(saveResult.getApiError());
        }

        // Log the update for a superuser role if necessary
        if (updatedRole.getIsSuperUser()) {
            log.info("Updated role to superuser: {}", saveResult.getData().getName());
        }

        return ResponseEntity.ok(saveResult.getData());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable UUID id) {
        ServiceResult<Void> roleDeleteResult = roleService.deleteById(id);
        if (!roleDeleteResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(roleDeleteResult.getApiError());
        }

        ServiceResult<Void> permissionDeleteResult = rolePermissionService.deleteById(id);
        if (!permissionDeleteResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(permissionDeleteResult.getApiError());
        }

        return ResponseEntity.noContent().build();
    }
}
