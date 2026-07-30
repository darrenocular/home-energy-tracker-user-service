package com.homeenergytracker.userservice.service;

import com.homeenergytracker.userservice.dto.UserDto;
import com.homeenergytracker.userservice.entity.User;
import com.homeenergytracker.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserDto input) {
        log.info("Creating user {}", input);

        final User createdUser = User.builder()
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .email(input.getEmail())
                .address(input.getAddress())
                .alerting(input.isAlerting())
                .energyAlertingThreshold(input.getEnergyAlertingThreshold())
                .build();

        final User saved = userRepository.save(createdUser);
        return toDto(saved);
    }

    public UserDto getUserById(Long id) {
        log.info("Retrieving user by id {}", id);
        return userRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public void updateUser(Long id, UserDto input) {
        log.info("Updating user {}", input);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setEmail(input.getEmail());
        user.setAddress(input.getAddress());
        user.setAlerting(input.isAlerting());
        user.setEnergyAlertingThreshold(input.getEnergyAlertingThreshold());
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .address(user.getAddress())
                .alerting(user.isAlerting())
                .energyAlertingThreshold(user.getEnergyAlertingThreshold())
                .build();
    }
}
