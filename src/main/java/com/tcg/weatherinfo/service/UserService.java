package com.tcg.weatherinfo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.weatherinfo.dto.UserDTO;
import com.tcg.weatherinfo.entity.User;
import com.tcg.weatherinfo.exception.UserNotFoundException;
import com.tcg.weatherinfo.exception.WeatherServiceException;
import com.tcg.weatherinfo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	@Transactional
	public User createUser(UserDTO userDTO) {
		if (userRepository.existsByUsername(userDTO.getUsername())) {
			throw new WeatherServiceException("Username already exists");
		}

		User user = User.builder().username(userDTO.getUsername())
				.password(userDTO.getPassword()).active(true).build();

		return userRepository.save(user);
	}

	@Transactional
	public void toggleUserStatus(Long userId, boolean active) {
		userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

		userRepository.updateUserActiveStatus(userId, active);
		log.info("Updated user {} status to {}", userId, active);
	}

	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found: " + username));
	}

	public Page<User> getAllUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}
}