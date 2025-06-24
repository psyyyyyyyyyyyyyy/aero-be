package com.sw.aero.domain.tourspot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.aero.domain.tourspot.dto.TourSpotInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class TourSpotInfoService {

    @Value("${tour.api.key}")
    private String serviceKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TourSpotInfo findTourSpotByName(String keyword) {
        try {
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String rawUrl = "https://apis.data.go.kr/B551011/KorWithService2/searchKeyword2"
                    + "?serviceKey=" + serviceKey
                    + "&MobileOS=ETC"
                    + "&MobileApp=aero"
                    + "&arrange=A"
                    + "&numOfRows=1"
                    + "&pageNo=1"
                    + "&_type=json"
                    + "&keyword=" + encodedKeyword;

            URI uri = new URI(rawUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            String response = responseEntity.getBody();

            System.out.println("📩 응답 원문: " + response);

            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items.isMissingNode() || !items.isArray() || items.size() == 0) {
                System.out.println("⚠️ 검색 결과 없음");
                return null;
            }

            JsonNode item = items.get(0);
            String address = item.path("addr1").asText(null);
            double mapX = item.path("mapx").asDouble();
            double mapY = item.path("mapy").asDouble();
            String imageUrl = item.path("firstimage").asText(null);

            return new TourSpotInfo(address, mapX, mapY, imageUrl);
        } catch (Exception e) {
            System.out.println("❌ 예외 발생: " + e.getMessage());
            return null;
        }
    }

}
