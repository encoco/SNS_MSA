package com.gatewayservice.controller;

import com.common.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenProvider tokenProvider;

    /**
     * 리프레시 토큰 재발급 엔드포인트 (Reactive 버전)
     */
    @PostMapping("/refresh")
    public Mono<ResponseEntity<Map<String,String>>> refreshToken(ServerWebExchange exchange) {
        // 1) 리액티브 방식으로 쿠키 꺼내기
        List<HttpCookie> cookies = exchange.getRequest().getCookies().get("refreshToken");
        if (cookies == null || cookies.isEmpty()) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
        String refreshToken = cookies.get(0).getValue();

        // 2) 토큰 유효성 검사
        if (!tokenProvider.validateToken(refreshToken)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        // 3) 새 액세스 토큰 발급
        Long userId   = (long) tokenProvider.getUserIdFromToken(refreshToken);
        String nickname = tokenProvider.getNicknameFromToken(refreshToken);

        // 4) 새 액세스 토큰 발급 (여기에 userId, nickname 전달)
        String newAccessToken = tokenProvider.createAccessToken(userId, nickname);

        // 4) 응답에 새 쿠키 세팅
        ResponseCookie newCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .build();

        // 5) 최종 응답
        return Mono.just(ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newCookie.toString())
                .body(Map.of("accessToken", newAccessToken))
        );
    }
}
