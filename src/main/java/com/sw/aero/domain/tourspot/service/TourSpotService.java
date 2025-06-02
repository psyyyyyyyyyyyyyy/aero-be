package com.sw.aero.domain.tourspot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.aero.domain.tourspot.entity.TourSpot;
import com.sw.aero.domain.tourspot.repository.TourSpotRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourSpotService {

    @Value("${tour.api.key}")
    private String apiKey;

    private final TourSpotRepository tourSpotRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> CATEGORY_CODES = List.of("AC", "C01", "EV", "EX", "FD", "HS", "LS", "NA", "SH", "VE");
    private static final List<String> REGION_CODES = List.of("11");

    private String getSafe(JsonNode node, String field) {
        JsonNode valueNode = node.get(field);
        return valueNode != null && !valueNode.isNull() ? valueNode.asText() : "";
    }

    public void importBarrierFreeTourSpots() {
        for (String regionCode : REGION_CODES) {
            for (String category : CATEGORY_CODES) {
                for (int page = 1; page <= 1; page++) {
                    try {
                        String listUrl = "https://apis.data.go.kr/B551011/KorWithService2/areaBasedList2"
                                + "?serviceKey=" + apiKey
                                + "&numOfRows=100"
                                + "&pageNo=" + page
                                + "&MobileOS=ETC"
                                + "&MobileApp=Aero"
                                + "&_type=json"
                                + "&lDongRegnCd=" + regionCode
                                + "&lclsSystm1=" + category;

                        URI uri = new URI(listUrl);

                        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
                        log.info("📦 LIST response: {}", response.getBody());

                        JsonNode root = objectMapper.readTree(response.getBody());
                        String resultCode = root.path("response").path("header").path("resultCode").asText();
                        if (!"0000".equals(resultCode)) break;

                        JsonNode items = root.path("response").path("body").path("items").path("item");
                        if (!items.isArray() || items.size() == 0) break;

                        List<TourSpot> spots = new ArrayList<>();
                        for (JsonNode item : items) {
                            String contentId = item.path("contentid").asText();

                            String detailUrl = "https://apis.data.go.kr/B551011/KorWithService2/detailWithTour2"
                                    + "?serviceKey=" + apiKey
                                    + "&MobileOS=ETC"
                                    + "&MobileApp=Aero"
                                    + "&_type=json"
                                    + "&contentId=" + contentId;

                            URI detailUri = new URI(detailUrl);

                            try {
                                ResponseEntity<String> detailResponse = restTemplate.getForEntity(detailUri, String.class);
                                JsonNode detailItemNode = objectMapper.readTree(detailResponse.getBody())
                                        .path("response").path("body").path("items").path("item");

                                if (detailItemNode.isArray() && detailItemNode.size() > 0) {
                                    detailItemNode = detailItemNode.get(0);
                                }

                                TourSpot spot = TourSpot.builder()
                                        .title(item.path("title").asText())
                                        .address(item.path("addr1").asText())
                                        .address2(item.path("addr2").asText())
                                        .areaCode(item.path("areacode").asText())
                                        .cat1(item.path("cat1").asText())
                                        .cat2(item.path("cat2").asText())
                                        .cat3(item.path("cat3").asText())
                                        .contentId(Long.parseLong(contentId))
                                        .contentTypeId(item.path("contenttypeid").asText())
                                        .createdTime(item.path("createdtime").asText())
                                        .copyrightDivCode(item.path("cpyrhtDivCd").asText())
                                        .mapX(item.path("mapx").asText())
                                        .mapY(item.path("mapy").asText())
                                        .mLevel(item.path("mlevel").asText())
                                        .modifiedTime(item.path("modifiedtime").asText())
                                        .sigunguCode(item.path("sigungucode").asText())
                                        .tel(item.path("tel").asText())
                                        .zipCode(item.path("zipcode").asText())
                                        .lDongSignguCd(item.path("lDongSignguCd").asText())
                                        .categoryCode(category)
                                        .categoryCode2(item.path("lclsSystm2").asText())
                                        .categoryCode3(item.path("lclsSystm3").asText())
                                        .firstImage(item.path("firstimage").asText(""))
                                        .firstImage2(item.path("firstimage2").asText(""))
                                        .parking(getSafe(detailItemNode, "parking"))
                                        .wheelchair(getSafe(detailItemNode, "wheelchair"))
                                        .restroom(getSafe(detailItemNode, "restroom"))
                                        .elevator(getSafe(detailItemNode, "elevator"))
                                        .stroller(getSafe(detailItemNode, "stroller"))
                                        .lactationroom(getSafe(detailItemNode, "lactationroom"))
                                        .guidesystem(getSafe(detailItemNode, "guidesystem"))
                                        .helpdog(getSafe(detailItemNode, "helpdog"))
                                        .braileblock(getSafe(detailItemNode, "braileblock"))
                                        .signguide(getSafe(detailItemNode, "signguide"))
                                        .videoguide(getSafe(detailItemNode, "videoguide"))
                                        .audioguide(getSafe(detailItemNode, "audioguide"))
                                        .exit(getSafe(detailItemNode, "exit"))
                                        .route(getSafe(detailItemNode, "route"))
                                        .publictransport(getSafe(detailItemNode, "publictransport"))
                                        .ticketoffice(getSafe(detailItemNode, "ticketoffice"))
                                        .promotion(getSafe(detailItemNode, "promotion"))
                                        .auditorium(getSafe(detailItemNode, "auditorium"))
                                        .room(getSafe(detailItemNode, "room"))
                                        .handicapetc(getSafe(detailItemNode, "handicapetc"))
                                        .guidehuman(getSafe(detailItemNode, "guidehuman"))
                                        .bigprint(getSafe(detailItemNode, "bigprint"))
                                        .brailepromotion(getSafe(detailItemNode, "brailepromotion"))
                                        .blindhandicapetc(getSafe(detailItemNode, "blindhandicapetc"))
                                        .hearingroom(getSafe(detailItemNode, "hearingroom"))
                                        .hearinghandicapetc(getSafe(detailItemNode, "hearinghandicapetc"))
                                        .babysparechair(getSafe(detailItemNode, "babysparechair"))
                                        .infantsfamilyetc(getSafe(detailItemNode, "infantsfamilyetc"))
                                        .build();

                                spots.add(spot);
                            } catch (Exception e) {
                                log.warn("❌ DETAIL 파싱 실패: contentId={}, 오류={}", contentId, e.getMessage());
                            }
                        }

                        tourSpotRepository.saveAll(spots);

                    } catch (Exception e) {
                        log.error("❌ 전체 요청 실패: region={}, category={}, page={}", regionCode, category, page);
                        log.error("에러 메시지: ", e);
                    }
                }
            }
        }
    }

    public Page<TourSpot> getFilteredTourSpots(String areaCode, String sigunguCode, List<String> facilityFilters, Pageable pageable) {
        return tourSpotRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (areaCode != null && !areaCode.isEmpty()) {
                predicates.add(cb.equal(root.get("areaCode"), areaCode));
            }

            if (sigunguCode != null && !sigunguCode.isEmpty()) {
                predicates.add(cb.equal(root.get("sigunguCode"), sigunguCode));
            }

            if (facilityFilters != null && !facilityFilters.isEmpty()) {
                for (String facility : facilityFilters) {
                    predicates.add(cb.notEqual(cb.trim(cb.lower(root.get(facility))), ""));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}
