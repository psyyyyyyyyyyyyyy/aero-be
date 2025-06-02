package com.sw.aero.domain.auth.controller;

import com.sw.aero.domain.auth.dto.KakaoLoginRequest;
import com.sw.aero.domain.auth.dto.KakaoUserResponse;
import com.sw.aero.domain.auth.service.AuthService;
import com.sw.aero.domain.auth.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoService kakaoService;
    private final AuthService authService;

    @PostMapping("/kakao")
    public KakaoUserResponse kakaoLogin(@RequestBody KakaoLoginRequest request) {
        System.out.println("🔥 Controller 도착!");
        return authService.kakaoLogin(request);
    }
}
