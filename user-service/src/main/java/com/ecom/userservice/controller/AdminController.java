package com.ecom.userservice.controller;



import com.ecom.userservice.dto.AuthResponse;
import com.ecom.userservice.dto.LoginRequest;
import com.ecom.userservice.dto.UserRequestDto;
import com.ecom.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> addAdmin(@RequestBody  UserRequestDto userRequestDto){
        return new ResponseEntity<>( userService.saveAdmin(userRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> adminLogin(@RequestBody LoginRequest loginRequest){
        return new ResponseEntity<>(userService.adminLogin(loginRequest),HttpStatus.OK);
    }
}
