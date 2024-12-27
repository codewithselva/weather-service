package com.tcg.weatherinfo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.tcg.weatherinfo.dto.UserDTO;
import com.tcg.weatherinfo.entity.User;
import com.tcg.weatherinfo.exception.UserNotFoundException;
import com.tcg.weatherinfo.repository.UserRepository;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserService userService = new UserService(userRepository);
    
    @Test
    void testCreateUser() {
    	UserDTO userDTO = UserDTO.builder()
    	        .username("testuser")
    	        .password("password")
    	        .active(true)
    	        .build();
        when(userRepository.existsByUsername("testuser")).thenReturn(false);

        User user = User.builder()
    	        .id(1L) // If needed, for existing entities
    	        .username("testuser")
    	        .password("encodedpassword")
    	        .active(true)
    	        .build();
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(userDTO);
        assertThat(createdUser.getUsername()).isEqualTo("testuser");
        assertThat(createdUser.getPassword()).isEqualTo("encodedpassword");
    }

    @Test
    void testToggleUserStatus() {
    	User user = User.builder()
    	        .id(1L) // If needed, for existing entities
    	        .username("testuser")
    	        .password("password")
    	        .active(true)
    	        .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.toggleUserStatus(1L, false);

        verify(userRepository).updateUserActiveStatus(1L, false);
    }

    @Test
    void testGetUserByUsername() {
    	User user = User.builder()
    	        .id(1L) // If needed, for existing entities
    	        .username("testuser")
    	        .password("password")
    	        .active(true)
    	        .build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByUsername("testuser");
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testuser");
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUsername("unknown"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
