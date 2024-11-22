package com.fusionalmerefc.service.impl;

import com.fusionalmerefc.config.ServiceResult;
import com.fusionalmerefc.model.Permission;
import com.fusionalmerefc.model.Role;
import com.fusionalmerefc.model.RolePermission;
import com.fusionalmerefc.repository.PermissionRepository;
import com.fusionalmerefc.repository.RolePermissionRepository;
import com.fusionalmerefc.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
public class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role baseRole;
    private Permission permission1;
    private Permission permission2;
    private RolePermission rolePermission1;
    private RolePermission rolePermission2;

    @BeforeEach
    public void setUp() {
        // Set up the permissions
        permission1 = new Permission();
        permission1.setUuid(UUID.randomUUID());
        permission1.setName("READ_ACCESS");

        permission2 = new Permission();
        permission2.setUuid(UUID.randomUUID());
        permission2.setName("WRITE_ACCESS");

        // Set up the base role
        baseRole = new Role();
        baseRole.setUuid(UUID.randomUUID());
        baseRole.setName("Base Role");
        baseRole.setDescription("Base role with permissions");
        baseRole.setIsSuperUser(false);

        // Set up the role permissions
        rolePermission1 = new RolePermission();
        rolePermission1.setRole(baseRole);
        rolePermission1.setPermission(permission1);

        rolePermission2 = new RolePermission();
        rolePermission2.setRole(baseRole);
        rolePermission2.setPermission(permission2);

        when(roleRepository.save(any(Role.class))).thenReturn(baseRole);
        when(permissionRepository.findAll()).thenReturn(List.of(permission1, permission2));
    }

    @Test
    public void testSaveRoleSuccessfully() {
        // Setup
        Role role = new Role();
        role.setUuid(UUID.randomUUID());
        role.setName("Test Role");
        role.setDescription("Role for testing save");
        role.setIsSuperUser(false);

        when(roleRepository.save(role)).thenReturn(role);

        ServiceResult<Role> result = roleService.save(role);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData().getName()).isEqualTo("Test Role");
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    public void testSaveSuperUserRoleWithPermissions() {
        // Setup
        Role superUserRole = new Role();
        superUserRole.setUuid(UUID.randomUUID());
        superUserRole.setName("Super User Role");
        superUserRole.setDescription("Super User Role with all permissions");
        superUserRole.setIsSuperUser(true);

        // When the role is a super user, all permissions should be assigned
        ServiceResult<Role> result = roleService.save(superUserRole);

        assertThat(result.isSuccess()).isTrue();
        verify(permissionRepository, times(1)).findAll();
        verify(rolePermissionRepository, times(2)).save(any(RolePermission.class)); // For both permissions
    }

    @Test
    public void testSaveRoleWithNullExternalIdentifier() {
        Role role = new Role();
        role.setName("Role with no extId");

        ServiceResult<Role> result = roleService.save(role);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getApiError().getMessage()).contains("Failed to save role");
    }

    @Test
    public void testSaveAllRolesSuccessfully() {
        Role role1 = new Role();
        role1.setName("Role 1");
        role1.setDescription("Role 1 Description");
        role1.setExternalIdentifier("Role1ExtId");

        Role role2 = new Role();
        role2.setName("Role 2");
        role2.setDescription("Role 2 Description");
        role2.setExternalIdentifier("Role2ExtId");

        List<Role> roles = List.of(role1, role2);

        when(roleRepository.save(any(Role.class))).thenReturn(role1, role2);

        ServiceResult<List<Role>> result = roleService.saveAll(roles);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData()).hasSize(2);
        assertThat(result.getData().get(0).getName()).isEqualTo("Role 1");
        assertThat(result.getData().get(1).getName()).isEqualTo("Role 2");
    }

    @Test
    public void testSaveAllRolesWithNullValues() {
        List<Role> roles = List.of(new Role(), new Role());
        
        assertThrows(IllegalArgumentException.class, () -> {
            roleService.saveAll(roles);
        });
    }

    @Test
    public void testSaveAllRolesWithDuplicateExternalIdentifier() {
        Role role1 = new Role();
        role1.setName("Role 1");
        role1.setExternalIdentifier("external1");

        Role role2 = new Role();
        role2.setName("Role 2");
        role2.setExternalIdentifier("external1"); // Duplicate identifier

        List<Role> roles = List.of(role1, role2);

        assertThrows(IllegalArgumentException.class, () -> {
            roleService.saveAll(roles);
        });

    }

    @Test
    public void testSaveAllRolesWithMissingName() {
        Role role1 = new Role();
        role1.setExternalIdentifier("external1");

        List<Role> roles = List.of(role1);

        assertThrows(IllegalArgumentException.class, () -> {
            roleService.saveAll(roles);
        });

    }

    @Test
    public void testAssignAllPermissionsToSuperUserRole() {
        Role superUserRole = new Role();
        superUserRole.setUuid(UUID.randomUUID());
        superUserRole.setName("Super User Role");
        superUserRole.setDescription("Super User Role");
        superUserRole.setIsSuperUser(true);

        roleService.save(superUserRole);

        verify(rolePermissionRepository, times(2)).save(any(RolePermission.class)); // Two permissions
    }

    @Test
    public void testHandleExceptionDuringSave() {
        Role role = new Role();
        role.setName("Faulty Role");

        when(roleRepository.save(any(Role.class))).thenThrow(new RuntimeException("Database error"));

        ServiceResult<Role> result = roleService.save(role);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getApiError().getMessage()).contains("Failed to save role");
    }
}
