package com.sw.aero.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KakaoUserResponse {
    private String token;
    private Long id;
    private KakaoAccount kakao_account;

    @Getter
    @Setter
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter
        @Setter
        public static class Profile {
            private String nickname;
            private String profile_image_url;
        }
    }
}
