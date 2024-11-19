package com.fusionalmerefc.repository;

import com.fusionalmerefc.model.Role;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseRepository<Role, UUID> {
    Role findByNameIgnoreCase(String name);
}
