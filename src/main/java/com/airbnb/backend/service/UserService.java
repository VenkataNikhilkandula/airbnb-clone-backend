package com.airbnb.backend.service;

import com.airbnb.backend.dto.request.UserLoginRequestDto;
import com.airbnb.backend.dto.request.UserRegisterRequestDto;
import com.airbnb.backend.dto.response.UserResponseDto;
import com.airbnb.backend.entity.User;
import com.airbnb.backend.exception.BadRequestException;
import com.airbnb.backend.exception.ResourceNotFoundException;
import com.airbnb.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDto registerUser(UserRegisterRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        User user = new User();
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(requestDto.getPassword()); // In a real app, hash this password
        user.setRole(requestDto.getRole());

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    public UserResponseDto loginUser(UserLoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getPassword().equals(requestDto.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        return mapToDto(user);
    }

    private UserResponseDto mapToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
