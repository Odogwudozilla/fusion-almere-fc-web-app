package com.fusionalmerefc.service.impl;

import com.fusionalmerefc.DTOs.DTOMapper;
import com.fusionalmerefc.DTOs.RoleDTO;
import com.fusionalmerefc.DTOs.UserDTO;
import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Role;
import com.fusionalmerefc.model.User;
import com.fusionalmerefc.model.UserRole;
import com.fusionalmerefc.model.constants.StatusType;
import com.fusionalmerefc.repository.RoleRepository;
import com.fusionalmerefc.repository.UserRepository;
import com.fusionalmerefc.repository.UserRoleRepository;
import com.fusionalmerefc.service.RoleService;
import com.fusionalmerefc.service.UserService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, UUID> implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    
    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, RoleService roleService) {
        super(userRepository); 
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    @Override
    public ServiceResult<List<User>> findByStatus(StatusType status) {
        return null;
    }
    
    @Override
    public ServiceResult<List<User>> saveAll(List<User> users) {
        ServiceResult<List<User>> result = new ServiceResult<>();

        // Validate users
        Set<String> seenIdentifiers = new HashSet<>();
        for (User user : users) {
            if (user.getExternalIdentifier() == null || user.getUsername() == null) {
                throw new IllegalArgumentException("Username and External Identifier cannot be null.");
            }
            if (!seenIdentifiers.add(user.getExternalIdentifier())) {
                throw new IllegalArgumentException("Duplicate External Identifier found: " + user.getExternalIdentifier());
            }
        }

        try {
            if (users == null || users.isEmpty()) {
                throw new IllegalArgumentException("User list cannot be null or empty.");
            }

            List<User> savedUsers = userRepository.saveAll(users);
            result.setData(savedUsers);
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setApiError(new ApiError("Failed to save users: " + ex.getMessage(), ApiErrorSeverity.ERROR));
        }

        return result;
    }
 
    @Override
    public ServiceResult<List<UserDTO>> mapFromUserToUserDTO(ServiceResult<List<User>> resultUsers) {
        ServiceResult<List<UserDTO>> resultDTO = new ServiceResult<>();

        List<User> users = resultUsers.getData();

        resultDTO.setData(users.stream().map(this::mapUserToUserDTO).collect(Collectors.toList()));
        resultDTO.setSuccess(resultUsers.isSuccess());
        resultDTO.setApiError(resultUsers.getApiError());
    
        return resultDTO;
    }

    @Override
    public ServiceResult<UserDTO> mapFromSingleUserToUserDTO(ServiceResult<User> resultUser) {
        ServiceResult<UserDTO> resultDTO = new ServiceResult<>();

        User user = resultUser.getData();

        resultDTO.setData(mapUserToUserDTO(user));
        resultDTO.setSuccess(resultUser.isSuccess());
        resultDTO.setApiError(resultUser.getApiError());
    
        return resultDTO;
    }

    public UserDTO mapUserToUserDTO(User user) {
        List<UserRole> userRoles = userRoleRepository.findByUserUuid(user.getUuid());
        // retrieve all the user assigned roles
        List<Role> assignedRoles = userRoles.stream().map(userRole -> {
            return userRole.getRole();
        }).collect(Collectors.toList());

        ServiceResult<List<Role>> roleResult = new ServiceResult<>();
        roleResult.setData(assignedRoles);

        ServiceResult<List<RoleDTO>> serviceResultDTO = roleService.convertRolesToRoleDTOs(roleResult); 
    
        // Construct the UserDTO
        UserDTO userDTO = new DTOMapper().mapToUserDTO(user);
        userDTO.setAssignedRoles(serviceResultDTO.getData());
        return userDTO;

    }

    @Override
    public List<Role> findRolesByExternalIdentifier(List<RoleDTO> roleDTOs) {
        List<Role> roles = new ArrayList<>();

        for (RoleDTO roleDTO : roleDTOs) {
            Optional<Role> optionalRole = roleRepository.findByExternalIdentifier(roleDTO.getExternalIdentifier());
            if (optionalRole.isPresent()) {
                roles.add(optionalRole.get());
            }
        }

        return roles;
    }

    @Override
    public void saveOrUpdateUserRoles(List<UserRole> toBeSavedUserRoles) {
        if (toBeSavedUserRoles.isEmpty()) {
            return; // Nothing to save or update
        }
    
        // Retrieve existing user roles for the given role
        UUID userUuid = toBeSavedUserRoles.get(0).getUser().getUuid();
        List<UserRole> existingUserRoles = userRoleRepository.findByUserUuid(userUuid);
    
        // Convert lists to sets for more efficient operations
        Set<UserRole> toBeSavedSet = new HashSet<>(toBeSavedUserRoles);
        Set<UserRole> existingSet = new HashSet<>(existingUserRoles);
    
        // Determine roles to delete
        Set<UserRole> toDelete = new HashSet<>(existingSet);
        toDelete.removeAll(toBeSavedSet); // Entries in existing but not in toBeSaved
    
        // Determine roles to save
        Set<UserRole> toSave = new HashSet<>(toBeSavedSet);
        toSave.removeAll(existingSet); // Entries in toBeSaved but not in existing
    
        // Perform delete and save operations
        if (!toDelete.isEmpty()) {
            userRoleRepository.deleteAll(toDelete);
        }
        if (!toSave.isEmpty()) {
            userRoleRepository.saveAll(toSave);
        }
    }

    @Override
    public ServiceResult<Page<User>> findAllWithPagination(int page, int pageSize, String sortField, String sortOrder) {
        // Convert sortOrder to Sort.Direction (ASC or DESC)
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortOrder);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.ASC; // Default to ASC if sortOrder is invalid
        }
    
        // Create a Pageable object with sorting
        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortField));
    
        // Fetch the paginated data from the repository
        Page<User> usersPage = userRepository.findAll(pageable);
    
        // Wrap the result in a ServiceResult and return it
        ServiceResult<Page<User>> result = new ServiceResult<>();
        result.setData(usersPage);
    
        return result;
    }
    
}
