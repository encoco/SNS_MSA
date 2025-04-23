package com.chatservice.client;

import com.common.dto.UsersInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/nickname/{userId}")
    String getNickname(@PathVariable("userId") int userId);

    @GetMapping("/api/users/profile/{userId}")
    String getProfileImg(@PathVariable("userId") int userId);

    @PostMapping("/api/users/nicknames")
    Map<Integer, String> getNicknames(@RequestBody List<Integer> userIds);

    @PostMapping("/api/users/profile-images")
    Map<Integer, String> getProfileImages(@RequestBody List<Integer> userIds);

    @GetMapping("/api/users/info/{userId}")
    ResponseEntity<UsersInfoDTO> getUserInfo(@PathVariable int userId);

    @GetMapping("/api/users/user-info")
    ResponseEntity<List<UsersInfoDTO>> getUserInfoList(@RequestParam("userIds") List<Long> userIds);
}