package com.fusionalmerefc.repository;

import com.fusionalmerefc.model.User;
import com.fusionalmerefc.model.constants.Status;

import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository userRepository;

    private Faker faker;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setUsername(faker.internet().username());
        user.setName(faker.name().fullName());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.internet().password());
        user.setWhatsappNumber(faker.phoneNumber().cellPhoneInternational());
        user.setStatus(Status.ACTIVE);
        log.info("The created user is {}",  user.toString());

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void testFindByUsername() {
        User user = new User();
        user.setUsername(faker.internet().username());
        user.setName(faker.name().fullName());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.internet().password());
        user.setWhatsappNumber(faker.phoneNumber().cellPhone());
        user.setStatus(Status.ACTIVE);

        userRepository.save(user);

        User foundUser = userRepository.findByUsername(user.getUsername());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
    }


}
