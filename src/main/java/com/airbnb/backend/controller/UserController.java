package com.airbnb.backend.controller;

import com.airbnb.backend.dto.request.UserLoginRequestDto;
import com.airbnb.backend.dto.request.UserRegisterRequestDto;
import com.airbnb.backend.dto.response.UserResponseDto;
import com.airbnb.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for user registration and login")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegisterRequestDto requestDto) {
        return new ResponseEntity<>(userService.registerUser(requestDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<UserResponseDto> loginUser(@Valid @RequestBody UserLoginRequestDto requestDto) {
        return ResponseEntity.ok(userService.loginUser(requestDto));
    }
}
