package com.fusionalmerefc.service.impl;

import com.fusionalmerefc.DTOs.DTOMapper;
import com.fusionalmerefc.config.ApiError;
import com.fusionalmerefc.config.ApiErrorSeverity;
import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.User;
import com.fusionalmerefc.model.constants.StatusType;
import com.fusionalmerefc.repository.UserRepository;
import com.fusionalmerefc.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, UUID> implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        super(userRepository); 
        this.userRepository = userRepository;
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
 

}
