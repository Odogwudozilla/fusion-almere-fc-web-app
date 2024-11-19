package com.fusionalmerefc.controller;

import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Permission;
import com.fusionalmerefc.service.PermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/bulk-add")
    public ResponseEntity<?> bulkAddPermissions(@RequestBody List<Permission> permissions) {
        ServiceResult<List<Permission>> result = permissionService.saveAll(permissions);

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getApiError());
        }

        return ResponseEntity.ok(result.getData());
    }

    @GetMapping
    public ResponseEntity<?> getAllPermissions() {
        ServiceResult<List<Permission>> result = permissionService.findAll();

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.getApiError());
        }

        return ResponseEntity.ok(result.getData());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPermissionById(@PathVariable UUID id) {
        ServiceResult<Optional<Permission>> result = permissionService.findById(id);

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.getApiError());
        }

        if (result.getData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("Permission not found with ID: " + id, ApiErrorSeverity.INFO));
        }

        return ResponseEntity.ok(result.getData());
    }

    @PostMapping
    public ResponseEntity<?> createPermission(@RequestBody Permission permission) {
        ServiceResult<Permission> result = permissionService.save(permission);

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getApiError());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getData());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePermission(@PathVariable UUID id, @RequestBody Permission updatedPermission) {
        ServiceResult<Optional<Permission>> existingPermissionResult = permissionService.findById(id);

        if (!existingPermissionResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(existingPermissionResult.getApiError());
        }

        if (existingPermissionResult.getData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("Permission not found with ID: " + id, ApiErrorSeverity.INFO));
        }

        // Update and save the permission
        updatedPermission.setUuid(id);
        ServiceResult<Permission> saveResult = permissionService.save(updatedPermission);

        if (!saveResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(saveResult.getApiError());
        }

        return ResponseEntity.ok(saveResult.getData());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable UUID id) {
        ServiceResult<Void> result = permissionService.deleteById(id);

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.getApiError());
        }

        return ResponseEntity.noContent().build();
    }
}
