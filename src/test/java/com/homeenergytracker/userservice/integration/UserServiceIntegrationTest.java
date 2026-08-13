package com.homeenergytracker.userservice.integration;

import com.homeenergytracker.userservice.dto.UserDto;
import com.homeenergytracker.userservice.entity.User;
import com.homeenergytracker.userservice.repository.UserRepository;
import com.homeenergytracker.userservice.service.UserService;
import com.homeenergytracker.userservice.testsupport.MySqlTestcontainersBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
public class UserServiceIntegrationTest extends MySqlTestcontainersBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_viaRestApi_persistsAndReturnsUser() {
        UserDto request = UserDto.builder()
                .firstName("Leet")
                .lastName("Journey")
                .email("leetjourney@gmail.com")
                .address("123 Coding St")
                .alerting(true)
                .energyAlertingThreshold(2000.0)
                .build();

        ResponseEntity<UserDto> response =
                restTemplate.postForEntity("/api/v1/users", request, UserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getFirstName()).isEqualTo("Leet");
        assertThat(response.getBody().getLastName()).isEqualTo("Journey");
        assertThat(response.getBody().getAddress()).isEqualTo("123 Coding St");
        assertThat(response.getBody().getEmail()).isEqualTo("leetjourney@gmail.com");
        assertThat(response.getBody().isAlerting()).isTrue();
        assertThat(response.getBody().getEnergyAlertingThreshold()).isEqualTo(2000.0);

        ResponseEntity<UserDto> loaded = restTemplate.getForEntity("/api/v1/users/" + response.getBody().getId(), UserDto.class);

        assertThat(loaded.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loaded.getBody()).isNotNull();
        assertThat(loaded.getBody().getEmail()).isEqualTo("leetjourney@gmail.com");
    }

    @Test
    void saveUser_viaRepository_roundTripsThroughMysql() {
        User saved = userRepository.save(User.builder()
                .firstName("Grace")
                .lastName("Hopper")
                .email("grace.it@example.com")
                .address("2 Compiler Way")
                .alerting(false)
                .energyAlertingThreshold(900.0)
                .build());

        assertThat(saved.getId()).isNotNull();

        User fromDb = userRepository.findById(saved.getId()).orElseThrow();
        assertThat(fromDb.getEmail()).isEqualTo("grace.it@example.com");
        assertThat(fromDb.getFirstName()).isEqualTo("Grace");
        assertThat(fromDb.isAlerting()).isFalse();
        assertThat(fromDb.getEnergyAlertingThreshold()).isEqualTo(900.0);
    }
}
