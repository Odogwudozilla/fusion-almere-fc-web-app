package com.fusionalmerefc.service.impl;

import com.fusionalmerefc.model.Status;
import com.fusionalmerefc.model.User;
import com.fusionalmerefc.repository.UserRepository;
import com.fusionalmerefc.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, UUID> implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        super(userRepository); 
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findByStatus(Status status) {
        return userRepository.findByStatus(status);
    }



}
