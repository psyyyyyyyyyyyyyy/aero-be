package com.sw.aero.domain.auth.controller;

import com.sw.aero.domain.aicourse.repository.AiCourseRepository;
import com.sw.aero.domain.auth.dto.GoogleLoginRequest;
import com.sw.aero.domain.auth.dto.KakaoLoginRequest;
import com.sw.aero.domain.auth.dto.KakaoLoginResponse;
import com.sw.aero.domain.auth.entity.RefreshToken;
import com.sw.aero.domain.auth.repository.RefreshTokenRepository;
import com.sw.aero.domain.auth.service.AuthService;
import com.sw.aero.domain.auth.service.KakaoService;
import com.sw.aero.domain.course.repository.CourseLikeRepository;
import com.sw.aero.domain.course.repository.UserCourseRepository;
import com.sw.aero.domain.tourspot.repository.TourSpotLikeRepository;
import com.sw.aero.domain.travellog.repository.TravelLogRepository;
import com.sw.aero.domain.user.entity.User;
import com.sw.aero.domain.user.repository.UserRepository;
import com.sw.aero.global.exception.NotFoundException;
import com.sw.aero.global.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final KakaoService kakaoService;
    private final AiCourseRepository aiCourseRepository;
    private final UserCourseRepository userCourseRepository;
    private final CourseLikeRepository courseLikeRepository;
    private final TravelLogRepository travelLogRepository;
    private final TourSpotLikeRepository tourSpotLikeRepository;

    @PostMapping("/kakao")
    public KakaoLoginResponse kakaoLogin(@RequestBody KakaoLoginRequest request) {
        return authService.kakaoLogin(request);
    }

    @PostMapping("/google")
    public KakaoLoginResponse googleLogin(@RequestBody GoogleLoginRequest request) {
        return authService.googleLogin(request);
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken) {
        String token = accessToken.replace("Bearer ", "");
        Long userId = jwtProvider.getUserIdFromToken(token);

        // 리프레시 토큰 삭제
        refreshTokenRepository.deleteById(userId);


        return ResponseEntity.ok("로그아웃 완료");
    }

    @Transactional
    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestHeader("Authorization") String accessToken) {
        String token = accessToken.replace("Bearer ", "");
        Long userId = jwtProvider.getUserIdFromToken(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("해당 유저 없음"));

        // 연관 데이터 삭제
        courseLikeRepository.deleteAllByUser(user);
        tourSpotLikeRepository.deleteAllByUser(user);
        travelLogRepository.deleteAllByUser(user);
        userCourseRepository.deleteAllByUser(user);
        aiCourseRepository.deleteAllByUser(user);

        // 리프레시 토큰 삭제
        refreshTokenRepository.deleteById(userId);

        // 유저 삭제
        userRepository.delete(user);

        return ResponseEntity.ok("회원탈퇴 완료");
    }


}
