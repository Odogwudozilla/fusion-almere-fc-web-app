package com.fusionalmerefc.repository;

import com.fusionalmerefc.model.RolePermission;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends BaseRepository<RolePermission, UUID> {
    List<RolePermission> findByRoleUuid(UUID roleUuid);

}
