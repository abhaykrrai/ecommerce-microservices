package com.ecom.userservice.service;

import com.ecom.userservice.dto.UserRequestDto;
import com.ecom.userservice.dto.UserResponseDto;
import com.ecom.userservice.entity.Role;
import com.ecom.userservice.entity.User;
import com.ecom.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String saveUser(UserRequestDto userRequestDto) {

        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            return "User already exists";
        }

        User user = new User();

        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());

        // Later we'll encrypt this using BCrypt
        user.setPassword(userRequestDto.getPassword());

        // Every newly registered user will be a USER
        user.setRole(Role.USER);

        userRepository.save(user);

        return "User Registered Successfully";
    }

    public List<UserResponseDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::entityToResponse)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserById(Long id) {

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return null;
        }

        return entityToResponse(user.get());
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(userRequestDto.getPassword());

        userRepository.save(user);

        return entityToResponse(user);
    }

    @Transactional
    public String deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            return "User not found";
        }

        userRepository.deleteById(id);

        return "User deleted successfully";
    }

    public UserResponseDto entityToResponse(User user) {

        UserResponseDto dto = new UserResponseDto();

        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        return dto;
    }
}