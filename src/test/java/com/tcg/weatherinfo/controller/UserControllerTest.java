package com.tcg.weatherinfo.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.tcg.weatherinfo.dto.UserDTO;
import com.tcg.weatherinfo.entity.User;
import com.tcg.weatherinfo.service.UserService;

@WebMvcTest(UserController.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private UserService userService;

	@Test
	void testCreateUser() throws Exception {
		// Given
		User user = User.builder().id(1L) // If needed, for existing entities
				.username("testuser").password("encodedpassword").active(true).build();

		when(userService.createUser(Mockito.any(UserDTO.class))).thenReturn(user);
		// When & Then
		mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\": \"testuser\", \"password\": \"password\"}")).andExpect(status().isCreated())
				.andExpect(jsonPath("$.username", is("testuser")));
	}

	@Test
	void testToggleUserStatus() throws Exception {
		mockMvc.perform(patch("/api/v1/users/1/status?active=false")).andExpect(status().isOk());
	}

	@Test
	void testGetAllUsers() throws Exception {
		User user = User.builder().id(1L) // If needed, for existing entities
				.username("testuser").password("encodedpassword").active(true).build();
		Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

		when(userService.getAllUsers(PageRequest.of(0, 10))).thenReturn(userPage);

		mockMvc.perform(get("/api/v1/users?page=0&size=10")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].username", is("testuser")));
	}

	@Test
	void testGetUserByUsername() throws Exception {
		User user = User.builder().id(1L) // If needed, for existing entities
				.username("testuser").password("encodedpassword").active(true).build();

		when(userService.getUserByUsername("testuser")).thenReturn(user);

		mockMvc.perform(get("/api/v1/users/testuser")).andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is("testuser")));
	}
}
