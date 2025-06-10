package com.sw.aero.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleUserResponse {
    private String sub; // Google의 고유 ID
    private String email;
    private String name;
    private String picture;
}
