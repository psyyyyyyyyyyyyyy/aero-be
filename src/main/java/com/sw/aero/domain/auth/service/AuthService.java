package com.sw.aero.domain.auth.service;

import com.sw.aero.domain.auth.dto.KakaoLoginRequest;
import com.sw.aero.domain.auth.dto.KakaoUserResponse;
import com.sw.aero.domain.user.entity.User;
import com.sw.aero.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoService kakaoService;
    private final UserRepository userRepository;

    public KakaoUserResponse kakaoLogin(KakaoLoginRequest request) {
        KakaoUserResponse kakaoUser = kakaoService.getUserInfo(request.getAccessToken());

        String socialId = String.valueOf(kakaoUser.getId());

        User user = User.builder()
                .socialId(socialId)
                .email(kakaoUser.getKakao_account().getEmail())
                .name(kakaoUser.getKakao_account().getProfile().getNickname())
                .profileImage(kakaoUser.getKakao_account().getProfile().getProfile_image_url())
                .provider("kakao")
                .build();

        User savedUser = userRepository.save(user);
        System.out.println("✅ 저장된 유저: " + savedUser.getId() + ", " + savedUser.getEmail());

        return kakaoUser;
    }
}
