package com.alarmservice.client;

import com.alarmservice.dto.UsersInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/info/{userId}")
    UsersInfoDTO getUserInfo(@PathVariable("userId") int userId);
}