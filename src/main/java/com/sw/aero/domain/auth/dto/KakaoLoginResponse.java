package com.sw.aero.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KakaoLoginResponse {
    private String accessToken;
    private String refreshToken;
}

