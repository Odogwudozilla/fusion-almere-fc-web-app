package com.fusionalmerefc.repository;

import com.fusionalmerefc.model.Role;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    private Faker faker;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
    }

    @Test
    public void testCreateRole() {
        Role role = new Role();
        role.setName(faker.job().title());
        role.setDescription(faker.lorem().sentence());

        Role savedRole = roleRepository.save(role);

        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getUuid()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo(role.getName());
    }

    @Test
    public void testFindByRoleName() {
        Role role = new Role();
        role.setName(faker.job().title());
        role.setDescription(faker.lorem().sentence());

        roleRepository.save(role);

        Role foundRole = roleRepository.findByNameIgnoreCase(role.getName());

        assertThat(foundRole).isNotNull();
        assertThat(foundRole.getName()).isEqualTo(role.getName());
    }
}
