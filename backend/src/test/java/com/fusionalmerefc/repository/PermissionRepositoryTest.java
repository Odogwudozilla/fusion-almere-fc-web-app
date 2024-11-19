package com.fusionalmerefc.repository;

import com.fusionalmerefc.model.Permission;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository permissionRepository;

    private Faker faker;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
    }

    @Test
    public void testCreatePermission() {
        Permission permission = new Permission();
        permission.setName(faker.lorem().word());
        permission.setDescription(faker.lorem().sentence());

        Permission savedPermission = permissionRepository.save(permission);

        assertThat(savedPermission).isNotNull();
        assertThat(savedPermission.getUuid()).isNotNull();
        assertThat(savedPermission.getName()).isEqualTo(permission.getName());
    }

    @Test
    public void testFindByPermissionName() {
        Permission permission = new Permission();
        permission.setName(faker.lorem().word());
        permission.setDescription(faker.lorem().sentence());

        permissionRepository.save(permission);

        Permission foundPermission = permissionRepository.findByNameIgnoreCase(permission.getName());

        assertThat(foundPermission).isNotNull();
        assertThat(foundPermission.getName()).isEqualTo(permission.getName());
    }
}
