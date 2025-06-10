package com.sw.aero.domain.auth.service;

import com.sw.aero.domain.auth.dto.GoogleUserResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleService {

    public GoogleUserResponse getUserInfo(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<GoogleUserResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                GoogleUserResponse.class
        );

        return response.getBody();
    }
}
