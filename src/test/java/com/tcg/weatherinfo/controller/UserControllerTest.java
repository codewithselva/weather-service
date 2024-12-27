package com.tcg.weatherinfo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcg.weatherinfo.dto.UserDTO;
import com.tcg.weatherinfo.entity.User;
import com.tcg.weatherinfo.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;
	private MockMvc mockMvc;

	private User user;
	private UserDTO userDTO;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(userController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()) .build();
		user = User.builder().id(1L).username("testuser").password("password").active(true).build();
		userDTO = UserDTO.builder().username("testuser").password("password").build();
	}

	@Test
	void testCreateUser() throws Exception {
		when(userService.createUser(any(UserDTO.class))).thenReturn(user);

		mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(userDTO))).andExpect(status().isCreated());

		verify(userService).createUser(any(UserDTO.class));
	}

	@Test
	void testToggleUserStatus() throws Exception {
		mockMvc.perform(patch("/api/v1/users/{userId}/status", 1L).param("active", "true")).andExpect(status().isOk());

		verify(userService).toggleUserStatus(anyLong(), anyBoolean());
	}

	@Test
	void testGetAllUsers() throws Exception {
		Pageable pageable = PageRequest.of(0, 10);
		Page<User> page = new PageImpl<>(Collections.singletonList(user), pageable, 1);
		when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/api/v1/users").param("page", "0").param("size", "10")).andExpect(status().isOk());

		verify(userService).getAllUsers(any(Pageable.class));
	}

	@Test
	void testGetUserByUsername() throws Exception {
		when(userService.getUserByUsername(anyString())).thenReturn(user);

		mockMvc.perform(get("/api/v1/users/{username}", "testuser")).andExpect(status().isOk());

		verify(userService).getUserByUsername(anyString());
	}
}
