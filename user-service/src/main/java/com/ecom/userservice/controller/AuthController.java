package com.ecom.userservice.controller;

import com.ecom.userservice.dto.AuthResponse;
import com.ecom.userservice.dto.LoginRequest;
import com.ecom.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
}