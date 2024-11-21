package com.fusionalmerefc.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fusionalmerefc.model.RolePermission;
import com.fusionalmerefc.repository.RolePermissionRepository;
import com.fusionalmerefc.service.RolePermissionService;

@Service
public class RolePermissionServiceImpl extends BaseServiceImpl<RolePermission, UUID> implements RolePermissionService {
    private RolePermissionRepository rolePermissionRepository;

    public RolePermissionServiceImpl(RolePermissionRepository rolePermissionRepository) {
        super(rolePermissionRepository);
        this.rolePermissionRepository = rolePermissionRepository;
    }

}
