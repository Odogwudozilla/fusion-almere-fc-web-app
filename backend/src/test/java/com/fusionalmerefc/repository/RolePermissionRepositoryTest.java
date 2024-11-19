package com.fusionalmerefc.repository;

import com.fusionalmerefc.model.RolePermission;
import com.fusionalmerefc.model.Role;
import com.fusionalmerefc.model.Permission;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RolePermissionRepositoryTest {

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    private Faker faker;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
    }

    @Test
    public void testCreateRolePermission() {
        Role role = new Role();
        role.setName(faker.job().title());
        role.setDescription(faker.lorem().sentence());
        roleRepository.save(role);

        Permission permission = new Permission();
        permission.setName(faker.lorem().word());
        permission.setDescription(faker.lorem().sentence());
        permissionRepository.save(permission);

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);

        RolePermission savedRolePermission = rolePermissionRepository.save(rolePermission);

        assertThat(savedRolePermission).isNotNull();
        assertThat(savedRolePermission.getUuid()).isNotNull();
        assertThat(savedRolePermission.getRole().getUuid()).isEqualTo(role.getUuid());
        assertThat(savedRolePermission.getPermission().getUuid()).isEqualTo(permission.getUuid());
    }
}
