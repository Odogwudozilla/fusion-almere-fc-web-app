package com.fusionalmerefc.repository;

import com.fusionalmerefc.model.Permission;

import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, UUID> {
    Permission findByNameIgnoreCase(String name);
}
