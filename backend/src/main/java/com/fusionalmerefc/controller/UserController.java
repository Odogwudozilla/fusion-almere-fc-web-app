package com.fusionalmerefc.controller;

import com.fusionalmerefc.DTOs.DTOMapper;
import com.fusionalmerefc.DTOs.UserDTO;
import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.User;
import com.fusionalmerefc.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final DTOMapper dtoMapper;

    public UserController(UserService userService, DTOMapper dtoMapper) {
        this.userService = userService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping("/bulk-add")
    public ResponseEntity<?> bulkAddUsers(@RequestBody List<UserDTO> usersDTO) {
        List<User> users = usersDTO.stream().map(this.dtoMapper::convertToUser).collect(Collectors.toList());
        return handleServiceResult(userService.saveAll(users), HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return handleServiceResult(userService.findAll(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/{externalIdentifier}")
    public ResponseEntity<?> getUserByExternalIdentifier(@PathVariable String externalIdentifier) {
        ServiceResult<Optional<User>> result = userService.findByExternalIdentifier(externalIdentifier);
        
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.getApiError());
        }

        if (result.getData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("User not found with externalIdentifier: " + externalIdentifier, ApiErrorSeverity.INFO));
        }

        return ResponseEntity.ok(result.getData());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        if (userDTO.getExternalIdentifier() == null) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST,
                    new ApiError("User externalIdentifier must not be empty", ApiErrorSeverity.INFO));
        }

        User user = dtoMapper.convertToUser(userDTO);
        return handleServiceResult(userService.save(user), HttpStatus.BAD_REQUEST, HttpStatus.CREATED);
    }

    @PutMapping("/{externalIdentifier}")
    public ResponseEntity<?> updateUser(
            @PathVariable String externalIdentifier,
            @RequestBody UserDTO updatedUserDTO) {

        if (externalIdentifier == null || updatedUserDTO.getExternalIdentifier() == null) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST,
                    new ApiError("User externalIdentifier must not be empty", ApiErrorSeverity.INFO));
        }

        // Fetch existing user
        ServiceResult<Optional<User>> existingUserResult = userService.findByExternalIdentifier(externalIdentifier);

        if (!existingUserResult.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, existingUserResult.getApiError());
        }

        Optional<User> existingUser = existingUserResult.getData();
        if (existingUser.isEmpty()) {
            return buildErrorResponse(HttpStatus.NOT_FOUND,
                    new ApiError("User not found with externalIdentifier: " + externalIdentifier, ApiErrorSeverity.INFO));
        }

        // Update and save the user
        User updatedUser = dtoMapper.convertToUser(updatedUserDTO);
        updatedUser.setUuid(existingUser.get().getUuid());
        return handleServiceResult(userService.save(updatedUser), HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{externalIdentifier}")
    public ResponseEntity<?> deleteUser(@PathVariable String externalIdentifier) {
        ServiceResult<Optional<User>> existingUserResult = userService.findByExternalIdentifier(externalIdentifier);

        if (!existingUserResult.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, existingUserResult.getApiError());
        }

        Optional<User> existingUser = existingUserResult.getData();
        if (existingUser.isEmpty()) {
            return buildErrorResponse(HttpStatus.NOT_FOUND,
                    new ApiError("User not found with externalIdentifier: " + externalIdentifier, ApiErrorSeverity.INFO));
        }

        return handleServiceResult(userService.deleteById(existingUser.get().getUuid()), HttpStatus.INTERNAL_SERVER_ERROR);
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
