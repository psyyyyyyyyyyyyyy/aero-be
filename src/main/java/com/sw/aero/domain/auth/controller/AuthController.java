package com.sw.aero.domain.auth.controller;

import com.sw.aero.domain.auth.dto.KakaoLoginRequest;
import com.sw.aero.domain.auth.dto.KakaoLoginResponse;
import com.sw.aero.domain.auth.entity.RefreshToken;
import com.sw.aero.domain.auth.repository.RefreshTokenRepository;
import com.sw.aero.domain.auth.service.AuthService;
import com.sw.aero.domain.auth.service.KakaoService;
import com.sw.aero.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoService kakaoService;
    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @PostMapping("/kakao")
    public KakaoLoginResponse kakaoLogin(@RequestBody KakaoLoginRequest request) {
        return authService.kakaoLogin(request);
    }

    @PostMapping("/reissue")
    public ResponseEntity<KakaoLoginResponse> reissue(@RequestHeader("Authorization") String refreshToken) {
        String token = refreshToken.replace("Bearer ", "");
        RefreshToken found = refreshTokenRepository.findAll().stream()
                .filter(rt -> rt.getToken().equals(token))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        String newAccessToken = jwtProvider.createToken(found.getUserId());

        return ResponseEntity.ok(new KakaoLoginResponse(newAccessToken, token));
    }


}
