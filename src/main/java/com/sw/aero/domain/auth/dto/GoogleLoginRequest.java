package com.sw.aero.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleLoginRequest {
    private String accessToken;
}
