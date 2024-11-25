package com.fusionalmerefc.repository;

import com.fusionalmerefc.model.UserRole;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends BaseRepository<UserRole, UUID> {
    List<UserRole> findByUserUuid(UUID uuid);
}
