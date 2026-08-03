package com.homeenergytracker.userservice;

import com.homeenergytracker.userservice.entity.User;
import com.homeenergytracker.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class UserServiceApplicationTests {

    @Autowired
    private UserRepository userRepository;

    private static final int NUMBER_OF_USERS = 10;

    @Test
    void contextLoads() {
    }

    @Disabled
    @Test
    void addUsersToDB() {
        for (int i = 1; i <= NUMBER_OF_USERS; i++) {
            User user = User.builder()
                    .firstName("User " + i)
                    .lastName("LastName" + i)
                    .email("user" + i + "@example.com")
                    .address(i + "Example St")
                    .alerting(i % 2 == 0)
                    .energyAlertingThreshold(1000.0 + i)
                    .build();
            userRepository.save(user);
        }
        log.info("User Repository has been populated");
    }
}
