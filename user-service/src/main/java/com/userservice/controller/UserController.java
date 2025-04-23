package com.userservice.controller;

import com.common.jwt.TokenProvider;
import com.common.security.AuthInfoUtil;
import com.userservice.config.JwtConfig;
import com.userservice.config.auth.PrincipalDetails;
import com.userservice.dto.SearchDTO;
import com.userservice.dto.UsersDTO;
import com.userservice.dto.UsersInfoDTO;
import com.userservice.entity.UsersEntity;
import com.userservice.repository.UsersRepository;
import com.userservice.service.UsersService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UsersRepository repository;
    private final UsersService usersService;

    @PostMapping("/checkId")
    public ResponseEntity<?> checkId(@RequestBody UsersDTO request) {
        boolean username = repository.existsByUsername(request.getUsername());
        boolean nickname = repository.existsByNickname(request.getNickname());
        if (username) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("isDuplicate", true, "message", "이미 사용 중인 아이디입니다."));
        } else if (nickname) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("isDuplicate", true, "message", "이미 사용 중인 닉네임입니다."));
        } else {
            // ID가 중복되지 않는 경우, 사용 가능하다는 응답을 보냅니다.
            return ResponseEntity.ok(Map.of("isDuplicate", false, "message", "사용 가능한 ID입니다."));
        }
    }

    @PostMapping("/Signup")
    public ResponseEntity<UsersDTO> signup(@RequestBody UsersDTO request) {
        usersService.registerUser(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/Logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", null); // 쿠키 이름을 refreshToken으로 변경
        refreshTokenCookie.setMaxAge(0); // 쿠키의 만료 시간을 0으로 설정하여 즉시 만료
        refreshTokenCookie.setPath("/"); // 모든 경로에서 유효한 쿠키로 설정
        response.addCookie(refreshTokenCookie); // 쿠키를 응답에 추가하여 클라이언트에 전송, 삭제됨을 알림

        return new ResponseEntity<>("You've been logged out successfully.", HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUser(@RequestParam("searchTerm") String searchTerm) {
        List<SearchDTO> searchResults = usersService.searchUsers(searchTerm);
        return ResponseEntity.ok(searchResults);
    }


    @PostMapping("/WriteProfile")
    public ResponseEntity<?> updateProfile(@ModelAttribute UsersDTO profile) {
        try {
            profile.setId(AuthInfoUtil.getUserId());
            UsersInfoDTO updatedProfile = usersService.updateUserProfile(profile);
            if (updatedProfile != null) {
                return ResponseEntity.internalServerError().body("닉네임 중복");
            }
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("프로필 업데이트 실패: " + e.getMessage());
        }
    }

    @PostMapping("/checkPassword")
    public ResponseEntity<?> checkPassword(@RequestBody UsersDTO dto) {
        boolean checked = usersService.updatePassword(dto, AuthInfoUtil.getUserId());
        if (checked) {
            return ResponseEntity.ok("완료");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호 확인");
        }
    }

    @PostMapping("/DeleteUser")
    public ResponseEntity<?> deleteUser() {
        try {
            usersService.DeleteUser(AuthInfoUtil.getUserId());
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("탈퇴 실패");
        }
    }

    @GetMapping("/info/{userId}")
    public ResponseEntity<UsersInfoDTO> getUserInfo(@PathVariable int userId) {
        UsersInfoDTO userInfo = usersService.findUserInfo(userId); // 서비스에서 DB 조회
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/info/bulk")
    public ResponseEntity<List<UsersInfoDTO>> getUsersInfo(@RequestBody List<Integer> userIds) {
        List<UsersEntity> users = repository.findAllById(userIds);

        List<UsersInfoDTO> result = users.stream()
                .map(user -> UsersInfoDTO.builder()
                        .id(user.getId())
                        .nickname(user.getNickname())
                        .img(user.getProfile_img())
                        .build())
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/nickname/{userId}")
    public ResponseEntity<String> getNickname(@PathVariable int userId) {
        String nickname = usersService.getNicknameById(userId);
        return ResponseEntity.ok(nickname);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<String> getProfileImg(@PathVariable int userId) {
        String profileImg = usersService.getProfileImgById(userId);
        return ResponseEntity.ok(profileImg);
    }

    @PostMapping("/nicknames")
    public Map<Integer, String> getNicknames(@RequestBody List<Integer> userIds) {
        return usersService.getNicknames(userIds);
    }

    @PostMapping("/profile-images")
    public Map<Integer, String> getProfileImages(@RequestBody List<Integer> userIds) {
        return usersService.getProfileImages(userIds);
    }

    @GetMapping("/user-info")
    public ResponseEntity<List<com.common.dto.UsersInfoDTO>> getUserInfoList(@RequestParam List<Long> userIds) {
        List<com.common.dto.UsersInfoDTO> userInfoList = usersService.getUserInfoList(userIds);
        return ResponseEntity.ok(userInfoList);
    }

}

