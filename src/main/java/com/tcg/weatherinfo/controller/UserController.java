package com.tcg.weatherinfo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.weatherinfo.dto.UserDTO;
import com.tcg.weatherinfo.entity.User;
import com.tcg.weatherinfo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Endpoints for user management")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Create new user",
            description = "Creates a new user account")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid user data")
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) {
        User user = userService.createUser(userDTO);
        return ResponseEntity.status(201).body(user);
    }

    @Operation(summary = "Toggle user status",
            description = "Activates or deactivates a user account")
    @PatchMapping("/{userId}/status")
    public ResponseEntity<Void> toggleUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean active) {
        userService.toggleUserStatus(userId, active);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all users",
            description = "Retrieves all users with pagination")
    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(summary = "Get user by username",
            description = "Retrieves user details by username")
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
}
