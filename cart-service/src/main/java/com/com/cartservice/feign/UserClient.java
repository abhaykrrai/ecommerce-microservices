package com.com.cartservice.feign;


import com.com.cartservice.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/user/{id}")
    public UserResponseDto getUserByID(@PathVariable Long id);
}
