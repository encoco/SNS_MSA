package com.userservice.config.auth;

import com.common.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.dto.UsersDTO;
import com.userservice.dto.UsersInfoDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();

        Long userId = (long) userDetails.getUsersDTO().getId();
        String nickname = userDetails.getUsersDTO().getNickname();

        String accessToken = tokenProvider.createAccessToken(userId, nickname);
        String refreshToken = tokenProvider.createRefreshToken(userId, nickname);
        UsersDTO userDTO = userDetails.getUsersDTO();
        UsersInfoDTO dto = UsersInfoDTO.toInfoDTO(userDTO);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken).httpOnly(true).path("/").secure(false).sameSite("Lax").build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        // Access Token만 JSON 응답으로 반환
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("nickname", dto); // UsersDTO 객체를 그대로 추가

        String tokensJson = new ObjectMapper().writeValueAsString(tokenMap);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(tokensJson); // 클라이언트로 응답 전송
        if (userDTO.getRole().equals("ROLE_USER_SNS")) {
            String encodedTokensJson = URLEncoder.encode(tokensJson, StandardCharsets.UTF_8);
            response.sendRedirect("http://www.grooo.kro.kr/?tokensJson=" + encodedTokensJson);
        }

    }
}
