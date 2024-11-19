package com.fusionalmerefc.repository;

import com.fusionalmerefc.model.Status;
import com.fusionalmerefc.model.User;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {
    User findByUsername(String username);
    
    List<User> findByStatus(Status status);
}
