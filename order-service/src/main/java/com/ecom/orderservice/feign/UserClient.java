package com.ecom.orderservice.feign;

import com.ecom.orderservice.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/user/{userId}")
    public UserResponseDto getUserById(@PathVariable Long userId);
}
