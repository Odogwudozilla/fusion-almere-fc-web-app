package com.fusionalmerefc.controller;

import com.fusionalmerefc.DTOs.DTOMapper;
import com.fusionalmerefc.DTOs.PermissionDTO;
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
    private final DTOMapper dtoMapper;

    public PermissionController(PermissionService permissionService, DTOMapper dtoMapper) {
        this.permissionService = permissionService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping("/bulk-add")
    public ResponseEntity<?> bulkAddPermissions(@RequestBody List<Permission> permissions) {
        return handleServiceResult(permissionService.saveAll(permissions), HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<?> getAllPermissions() {
        return handleServiceResult(permissionService.findAll(), HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<?> createPermission(@RequestBody PermissionDTO permissionDTO) {
        if (permissionDTO.getExternalIdentifier() == null) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST,
                    new ApiError("Permission externalIdentifier must not be empty", ApiErrorSeverity.INFO));
        }

        Permission permission = dtoMapper.convertToPermission(permissionDTO);
        return handleServiceResult(permissionService.save(permission), HttpStatus.BAD_REQUEST, HttpStatus.CREATED);
    }

    @PutMapping("/{externalIdentifier}")
    public ResponseEntity<?> updatePermission(
            @PathVariable String externalIdentifier,
            @RequestBody PermissionDTO updatedPermissionDTO) {

        if (externalIdentifier == null || updatedPermissionDTO.getExternalIdentifier() == null) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST,
                    new ApiError("Permission externalIdentifier must not be empty", ApiErrorSeverity.INFO));
        }

        // Fetch existing permission
        ServiceResult<Optional<Permission>> existingPermissionResult = permissionService.findByExternalIdentifier(externalIdentifier);

        if (!existingPermissionResult.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, existingPermissionResult.getApiError());
        }

        Optional<Permission> existingPermission = existingPermissionResult.getData();
        if (existingPermission.isEmpty()) {
            return buildErrorResponse(HttpStatus.NOT_FOUND,
                    new ApiError("Permission not found with External Identifier: " + externalIdentifier, ApiErrorSeverity.INFO));
        }

        // Update and save the permission
        Permission updatedPermission = dtoMapper.convertToPermission(updatedPermissionDTO);
        updatedPermission.setUuid(existingPermission.get().getUuid());
        return handleServiceResult(permissionService.save(updatedPermission), HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{externalIdentifier}")
    public ResponseEntity<?> deletePermission(@PathVariable String externalIdentifier) {
        ServiceResult<Optional<Permission>> existingPermissionResult = permissionService.findByExternalIdentifier(externalIdentifier);

        if (!existingPermissionResult.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, existingPermissionResult.getApiError());
        }

        Optional<Permission> existingPermission = existingPermissionResult.getData();
        if (existingPermission.isEmpty()) {
            return buildErrorResponse(HttpStatus.NOT_FOUND,
                    new ApiError("Permission not found with External Identifier: " + externalIdentifier, ApiErrorSeverity.INFO));
        }

        return handleServiceResult(permissionService.deleteById(existingPermission.get().getUuid()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Helper method to handle service results and build appropriate responses.
     */
    private <T> ResponseEntity<?> handleServiceResult(ServiceResult<T> result, HttpStatus failureStatus) {
        return handleServiceResult(result, failureStatus, HttpStatus.OK);
    }

    private <T> ResponseEntity<?> handleServiceResult(ServiceResult<T> result, HttpStatus failureStatus, HttpStatus successStatus) {
        return result.isSuccess()
                ? ResponseEntity.status(successStatus).body(result.getData())
                : buildErrorResponse(failureStatus, result.getApiError());
    }

    private ResponseEntity<ApiError> buildErrorResponse(HttpStatus status, ApiError apiError) {
        return ResponseEntity.status(status).body(apiError);
    }
}
