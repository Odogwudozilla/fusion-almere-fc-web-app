package com.fusionalmerefc.repository;

import com.fusionalmerefc.model.RolePermission;

import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends BaseRepository<RolePermission, UUID> {

}
