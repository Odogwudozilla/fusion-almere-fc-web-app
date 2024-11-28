package com.fusionalmerefc.service;

import com.fusionalmerefc.DTOs.RoleDTO;
import com.fusionalmerefc.DTOs.UserDTO;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Role;
import com.fusionalmerefc.model.User;
import com.fusionalmerefc.model.UserRole;
import com.fusionalmerefc.model.constants.StatusType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserService extends BaseService<User, UUID> {
    ServiceResult<List<User>> saveAll(List<User> users);
    ServiceResult<List<User>> findByStatus(StatusType status);
    ServiceResult<Optional<User>> findByExternalIdentifier(String externalIdentifier);
    ServiceResult<List<UserDTO>> mapFromUserToUserDTO(ServiceResult<List<User>> result);
    List<Role> findRolesByExternalIdentifier(List<RoleDTO> roleDTOs);
    void saveOrUpdateUserRoles(List<UserRole> toBeSavedUserRoles);
    ServiceResult<UserDTO> mapFromSingleUserToUserDTO(ServiceResult<User> result);
    
}
