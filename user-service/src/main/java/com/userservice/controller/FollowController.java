package com.userservice.controller;

import com.common.security.AuthInfoUtil;
import com.userservice.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @GetMapping("/exists")
    ResponseEntity<Boolean> checkFollow(@RequestParam("followerId") int followerId, @RequestParam("followingId") int followingId){
        boolean result = followService.exists(followerId, followingId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> follow(@PathVariable int userId) {
        if (followService.exists(AuthInfoUtil.getUserId(), userId)) {
            followService.unfollow(AuthInfoUtil.getUserId(), userId);
            return ResponseEntity.ok("unfollowed");
        } else {
            followService.follow(AuthInfoUtil.getUserId(), userId);
            return ResponseEntity.ok("followed");
        }
    }

    @GetMapping("/following-ids/{userId}")
    public ResponseEntity<List<Integer>> getFollowingIds(@PathVariable int userId) {
        return ResponseEntity.ok(followService.getFollowingIds(userId));
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<Map<String, Integer>> getFollowCount(@PathVariable int userId) {
        int followerCount = followService.countFollowers(userId);
        int followingCount = followService.countFollowing(userId);
        System.out.println("Follower count: " + followerCount);
        System.out.println("Following count: " + followingCount);
        Map<String, Integer> result = new HashMap<>();
        result.put("followerCount", followerCount);
        result.put("followingCount", followingCount);
        return ResponseEntity.ok(result);
    }

}
