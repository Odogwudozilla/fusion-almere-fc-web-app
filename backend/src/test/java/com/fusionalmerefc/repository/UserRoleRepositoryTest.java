package com.fusionalmerefc.repository;

import com.fusionalmerefc.model.UserRole;
import com.fusionalmerefc.model.constants.StatusType;
import com.fusionalmerefc.model.User;
import com.fusionalmerefc.model.Role;

import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRoleRepositoryTest {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Faker faker;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
    }

    @Test
    public void testCreateUserRole() {
        User user = new User();
        user.setUsername(faker.internet().username());
        user.setName(faker.name().fullName());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.internet().password());
        user.setWhatsappNumber(faker.phoneNumber().cellPhone());
        user.setStatus(StatusType.ACTIVE);
        userRepository.save(user);

        Role role = new Role();
        role.setName(faker.job().title());
        role.setDescription(faker.lorem().sentence());
        roleRepository.save(role);

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        UserRole savedUserRole = userRoleRepository.save(userRole);

        assertThat(savedUserRole).isNotNull();
        assertThat(savedUserRole.getUuid()).isNotNull();
        assertThat(savedUserRole.getUser().getUuid()).isEqualTo(user.getUuid());
        assertThat(savedUserRole.getRole().getUuid()).isEqualTo(role.getUuid());
    }
}
