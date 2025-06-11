package com.sw.aero.domain.auth.service;

import com.sw.aero.domain.auth.dto.*;
import com.sw.aero.domain.auth.entity.RefreshToken;
import com.sw.aero.domain.auth.repository.RefreshTokenRepository;
import com.sw.aero.domain.user.entity.SocialType;
import com.sw.aero.domain.user.entity.User;
import com.sw.aero.domain.user.repository.UserRepository;
import com.sw.aero.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public KakaoLoginResponse kakaoLogin(KakaoLoginRequest request) {
        KakaoUserResponse kakaoUser = kakaoService.getUserInfo(request.getAccessToken());
        String socialId = String.valueOf(kakaoUser.getId());

        User user = userRepository.findBySocialId(socialId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(kakaoUser.getKakao_account().getEmail())
                                .name(kakaoUser.getKakao_account().getProfile().getNickname())
                                .socialId(socialId)
                                .socialType(SocialType.KAKAO)
                                .build()
                ));

        String accessToken = jwtProvider.createToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken();

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .token(refreshToken)
                        .build()
        );

        return new KakaoLoginResponse(accessToken, refreshToken);
    }

    public KakaoLoginResponse googleLogin(GoogleLoginRequest request) {
        GoogleUserResponse googleUser = googleService.getUserInfo(request.getAccessToken());
        String socialId = googleUser.getSub();

        User user = userRepository.findBySocialId(socialId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(googleUser.getEmail())
                                .name(googleUser.getName())
                                .socialId(socialId)
                                .socialType(SocialType.GOOGLE)
                                .build()
                ));

        String accessToken = jwtProvider.createToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken();

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .token(refreshToken)
                        .build()
        );

        return new KakaoLoginResponse(accessToken, refreshToken);
    }
}


