package com.fusionalmerefc.controller;

import com.fusionalmerefc.DTOs.DTOMapper;
import com.fusionalmerefc.DTOs.UserDTO;
import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Role;
import com.fusionalmerefc.model.User;
import com.fusionalmerefc.model.UserRole;
import com.fusionalmerefc.service.UserRoleService;
import com.fusionalmerefc.service.UserService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final String RESOURCE_LIST_KEY = "users";
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final DTOMapper dtoMapper;

    public UserController(UserService userService, UserRoleService userRoleService, DTOMapper dtoMapper) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping("/bulk-add")
    public ResponseEntity<?> bulkAddUsers(@RequestBody List<UserDTO> usersDTO) {
        List<User> users = usersDTO.stream().map(this.dtoMapper::mapToUser).collect(Collectors.toList());
        return handleServiceResult(userService.saveAll(users), HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "ASC") String sortOrder) {

        // Fetch paginated data with sorting
        ServiceResult<Page<User>> result = userService.findAllWithPagination(page, pageSize, sortField, sortOrder);

        if (!result.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, result.getApiError());
        }

        // Extract the list of users from the Page<User>
        ServiceResult<List<User>> users = new ServiceResult<>();
        users.setData(result.getData().getContent());

        // Map the list of users to a list of UserDTOs
        ServiceResult<List<UserDTO>> userDtoResult = userService.mapFromUserToUserDTO(users);

        // Prepare response
        long totalItems = result.getData().getTotalElements();
        int responseSize = result.getData().getContent().size();
        long start = page * pageSize;
        long end = start + responseSize - 1; // Adjust end to response size

        // Wrap the paginated data and metadata
        Map<String, Object> response = new HashMap<>();
        response.put(RESOURCE_LIST_KEY, userDtoResult.getData());  // Assuming DTOs are in `data`
        response.put("total", totalItems);
        response.put("page", page + 1);                 // Return 1-based page index
        response.put("pageSize", responseSize);         // Return actual page size

        // Add Content-Range header (format: users <start>-<end>/<total>)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Range", RESOURCE_LIST_KEY + " " + start + "-" + end + "/" + totalItems);

        return ResponseEntity.ok().headers(headers).body(response);
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
    public ResponseEntity<?> createUser(@RequestBody UserDTO incomingUserDTO) {
        if (incomingUserDTO.getExternalIdentifier() == null) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST,
                    new ApiError("User externalIdentifier must not be empty", ApiErrorSeverity.INFO));
        }

        User user = dtoMapper.mapToUser(incomingUserDTO);
        List<Role> assignedRoles = userService.findRolesByExternalIdentifier(incomingUserDTO.getAssignedRoles());
        List<UserRole> toBeSavedUserRoles = dtoMapper.convertFromUserDTO(user, incomingUserDTO, assignedRoles);

        ServiceResult<User> result = userService.save(user);
        userService.saveOrUpdateUserRoles(toBeSavedUserRoles);

        if (!result.isSuccess()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, result.getApiError());
        }

        ServiceResult<UserDTO> savedUser = userService.mapFromSingleUserToUserDTO(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
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
        User updatedUser = dtoMapper.mapToUser(updatedUserDTO);
        updatedUser.setUuid(existingUser.get().getUuid());
        List<Role> assignedRoles = userService.findRolesByExternalIdentifier(updatedUserDTO.getAssignedRoles());
        List<UserRole> toBeSavedUserRoles = dtoMapper.convertFromUserDTO(updatedUser, updatedUserDTO, assignedRoles);

        ServiceResult<User> result = userService.save(updatedUser);
        userService.saveOrUpdateUserRoles(toBeSavedUserRoles);

        if (!result.isSuccess()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, result.getApiError());
        }

        ServiceResult<UserDTO> savedUser = userService.mapFromSingleUserToUserDTO(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
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

        // First, delete the associated user roles before deleting the user itself
        ServiceResult<UserRole> userRoleDeleteResult = userRoleService.deleteByUuid(existingUser.get().getUuid());
        if (!userRoleDeleteResult.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, userRoleDeleteResult.getApiError());
        }
    
        // Now, delete the user after removing its roles
        ServiceResult<Void> userDeleteResult = userService.deleteById(existingUser.get().getUuid());
        if (!userDeleteResult.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, userDeleteResult.getApiError());
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * ShowProfile endpoint: Retrieve user details for viewing the profile.
     */
    @GetMapping("/{externalIdentifier}/profile")
    public ResponseEntity<?> showProfile(@PathVariable String externalIdentifier) {
        ServiceResult<Optional<User>> result = userService.findByExternalIdentifier(externalIdentifier);

        if (!result.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, result.getApiError());
        }

        Optional<User> user = result.getData();
        if (user.isEmpty()) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, 
                new ApiError("User not found with identifier: " + externalIdentifier, ApiErrorSeverity.INFO));
        }

        UserDTO userDTO = dtoMapper.mapToUserDTO(user.get());
        return ResponseEntity.ok(userDTO);
    }

    /**
     * EditProfile endpoint: Update user details.
     */
    @PutMapping("/{externalIdentifier}/profile")
    public ResponseEntity<?> editProfile(
            @PathVariable String externalIdentifier, 
            @RequestBody UserDTO updatedUserDTO) {

        ServiceResult<Optional<User>> result = userService.findByExternalIdentifier(externalIdentifier);

        if (!result.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, result.getApiError());
        }

        Optional<User> userOptional = result.getData();
        if (userOptional.isEmpty()) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, 
                new ApiError("User not found with identifier: " + externalIdentifier, ApiErrorSeverity.INFO));
        }

        User userToUpdate = userOptional.get();

        // Apply updates only to relevant fields
        if (updatedUserDTO.getName() != null) userToUpdate.setName(updatedUserDTO.getName());
        if (updatedUserDTO.getEmail() != null) userToUpdate.setEmail(updatedUserDTO.getEmail());
        // Add more fields if needed

        ServiceResult<User> updateResult = userService.save(userToUpdate);
        if (!updateResult.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, updateResult.getApiError());
        }

        UserDTO updatedDTO = dtoMapper.mapToUserDTO(updateResult.getData());
        return ResponseEntity.ok(updatedDTO);
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
