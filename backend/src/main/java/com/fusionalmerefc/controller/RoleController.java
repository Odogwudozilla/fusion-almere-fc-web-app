package com.fusionalmerefc.controller;

import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Role;
import com.fusionalmerefc.service.RoleService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/bulk-add")
    public ResponseEntity<?> bulkAddRoles(@RequestBody List<Role> roles) {
        ServiceResult<List<Role>> result = roleService.saveAll(roles);

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getApiError());
        }

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
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        ServiceResult<Role> result = roleService.save(role);

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getApiError());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getData());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable UUID id, @RequestBody Role updatedRole) {
        ServiceResult<Optional<Role>> existingRoleResult = roleService.findById(id);

        if (!existingRoleResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(existingRoleResult.getApiError());
        }

        if (existingRoleResult.getData().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("Role not found with ID: " + id, ApiErrorSeverity.INFO));
        }

        // Update and save the role
        updatedRole.setUuid(id);
        ServiceResult<Role> saveResult = roleService.save(updatedRole);

        if (!saveResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(saveResult.getApiError());
        }

        return ResponseEntity.ok(saveResult.getData());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable UUID id) {
        ServiceResult<Void> result = roleService.deleteById(id);

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.getApiError());
        }

        return ResponseEntity.noContent().build();
    }
    
}
