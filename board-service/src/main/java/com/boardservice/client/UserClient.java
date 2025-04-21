package com.boardservice.client;


import com.common.dto.UsersInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/info/{userId}")
    UsersInfoDTO getUserInfo(@PathVariable("userId") int userId);

    @PostMapping("/api/users/info/bulk")
    List<UsersInfoDTO> getUsersInfo(@RequestBody List<Integer> userIds);

    @GetMapping("/api/users/follow/exists")
    boolean checkFollow(
            @RequestParam("followerId") int followerId,
            @RequestParam("followingId") int followingId
    );

    @GetMapping("/api/users/follow/following-ids/{userId}")
    List<Integer> getFollowingIds(@PathVariable("userId") int userId);

    @GetMapping("/api/users/follow/follower-ids/{userId}")
    List<Integer> getFollowerIds(@PathVariable("userId") int userId);
}