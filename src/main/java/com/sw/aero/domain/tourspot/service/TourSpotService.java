package com.sw.aero.domain.tourspot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.aero.domain.tourspot.dto.TourSpotResponse;
import com.sw.aero.domain.tourspot.dto.TourSpotSearchResponse;
import com.sw.aero.domain.tourspot.entity.TourSpot;
import com.sw.aero.domain.tourspot.repository.TourSpotLikeRepository;
import com.sw.aero.domain.tourspot.repository.TourSpotRepository;
import com.sw.aero.domain.user.entity.User;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourSpotService {

    @Value("${tour.api.key}")
    private String apiKey;

    private final TourSpotRepository tourSpotRepository;
    private final TourSpotLikeRepository tourSpotLikeRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> CATEGORY_CODES = List.of("AC", "EV", "EX", "FD", "HS", "LS", "NA", "SH", "VE");

    private String getSafe(JsonNode node, String field) {
        JsonNode valueNode = node.get(field);
        return valueNode != null && !valueNode.isNull() ? valueNode.asText() : "";
    }

    public void importBarrierFreeTourSpots(List<String> regionCodes) {
        for (String regionCode : regionCodes) {
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
                        log.info("\uD83D\uDCE6 LIST response: {}", response.getBody());

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
                                log.warn("\u274C DETAIL \uD30C\uC2F1 \uC2E4\uD328: contentId={}, \uC624\uB958={}", contentId, e.getMessage());
                            }
                        }

                        tourSpotRepository.saveAll(spots);

                    } catch (Exception e) {
                        log.error("\u274C \uC804\uCCB4 \uC694\uCCAD \uC2E4\uD328: region={}, category={}, page={}", regionCode, category, page);
                        log.error("\uC5D0\uB7EC \uBA54\uC2DC\uC9C0: ", e);
                    }
                }
            }
        }
    }

    public Page<TourSpotResponse> getFilteredTourSpots(
            String areaCode,
            String sigunguCode,
            List<String> facilityFilters,
            List<String> themeFilters,
            Pageable pageable,
            User user,
            String sortBy
    ) {
        Page<TourSpot> spots = tourSpotRepository.findAll((root, query, cb) -> {
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
            if (themeFilters != null && !themeFilters.isEmpty()) {
                predicates.add(root.get("categoryCode").in(themeFilters));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        Set<Long> likedSpotIds = user == null
                ? Collections.emptySet()
                : tourSpotLikeRepository.findAllByUser(user).stream()
                .map(like -> like.getTourSpot().getId())
                .collect(Collectors.toSet());

        if ("likes".equals(sortBy)) {
            // 전체 필터 조건으로 페이징 없이 다시 조회
            List<TourSpot> allSpots = tourSpotRepository.findAll((root, query, cb) -> {
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
                if (themeFilters != null && !themeFilters.isEmpty()) {
                    predicates.add(root.get("categoryCode").in(themeFilters));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            });

            List<TourSpotResponse> responses = allSpots.stream()
                    .map(spot -> {
                        long likeCount = tourSpotLikeRepository.countByTourSpotId(spot.getId());
                        boolean liked = likedSpotIds.contains(spot.getId());
                        return TourSpotResponse.from(spot, likeCount, liked);
                    })
                    .sorted(Comparator.comparingLong(TourSpotResponse::getLikeCount).reversed())
                    .toList();

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), responses.size());
            List<TourSpotResponse> pageContent = responses.subList(start, end);

            return new PageImpl<>(pageContent, pageable, responses.size()); // ✅ 정확한 전체 개수 전달
        }


        return spots.map(spot -> {
            long likeCount = tourSpotLikeRepository.countByTourSpotId(spot.getId());
            boolean liked = likedSpotIds.contains(spot.getId());
            return TourSpotResponse.from(spot, likeCount, liked);
        });
    }


    public Map<String, String> getBarrierFreeInfoByTourSpotId(Long contentId) {
        TourSpot spot = tourSpotRepository.findByContentId(contentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관광지를 찾을 수 없습니다: " + contentId));

        Map<String, String> map = new HashMap<>();

        if (spot.getParking() != null && !spot.getParking().isBlank()) map.put("parking", spot.getParking());
        if (spot.getWheelchair() != null && !spot.getWheelchair().isBlank())
            map.put("wheelchair", spot.getWheelchair());
        if (spot.getRestroom() != null && !spot.getRestroom().isBlank()) map.put("restroom", spot.getRestroom());
        if (spot.getElevator() != null && !spot.getElevator().isBlank()) map.put("elevator", spot.getElevator());
        if (spot.getStroller() != null && !spot.getStroller().isBlank()) map.put("stroller", spot.getStroller());
        if (spot.getLactationroom() != null && !spot.getLactationroom().isBlank())
            map.put("lactationroom", spot.getLactationroom());
        if (spot.getGuidesystem() != null && !spot.getGuidesystem().isBlank())
            map.put("guidesystem", spot.getGuidesystem());
        if (spot.getHelpdog() != null && !spot.getHelpdog().isBlank()) map.put("helpdog", spot.getHelpdog());
        if (spot.getBraileblock() != null && !spot.getBraileblock().isBlank())
            map.put("braileblock", spot.getBraileblock());
        if (spot.getSignguide() != null && !spot.getSignguide().isBlank()) map.put("signguide", spot.getSignguide());
        if (spot.getVideoguide() != null && !spot.getVideoguide().isBlank())
            map.put("videoguide", spot.getVideoguide());
        if (spot.getAudioguide() != null && !spot.getAudioguide().isBlank())
            map.put("audioguide", spot.getAudioguide());
        if (spot.getExit() != null && !spot.getExit().isBlank()) map.put("exit", spot.getExit());
        if (spot.getRoute() != null && !spot.getRoute().isBlank()) map.put("route", spot.getRoute());
        if (spot.getPublictransport() != null && !spot.getPublictransport().isBlank())
            map.put("publictransport", spot.getPublictransport());
        if (spot.getTicketoffice() != null && !spot.getTicketoffice().isBlank())
            map.put("ticketoffice", spot.getTicketoffice());
        if (spot.getPromotion() != null && !spot.getPromotion().isBlank()) map.put("promotion", spot.getPromotion());
        if (spot.getAuditorium() != null && !spot.getAuditorium().isBlank())
            map.put("auditorium", spot.getAuditorium());
        if (spot.getRoom() != null && !spot.getRoom().isBlank()) map.put("room", spot.getRoom());
        if (spot.getHandicapetc() != null && !spot.getHandicapetc().isBlank())
            map.put("handicapetc", spot.getHandicapetc());
        if (spot.getGuidehuman() != null && !spot.getGuidehuman().isBlank())
            map.put("guidehuman", spot.getGuidehuman());
        if (spot.getBigprint() != null && !spot.getBigprint().isBlank()) map.put("bigprint", spot.getBigprint());
        if (spot.getBrailepromotion() != null && !spot.getBrailepromotion().isBlank())
            map.put("brailepromotion", spot.getBrailepromotion());
        if (spot.getBlindhandicapetc() != null && !spot.getBlindhandicapetc().isBlank())
            map.put("blindhandicapetc", spot.getBlindhandicapetc());
        if (spot.getHearingroom() != null && !spot.getHearingroom().isBlank())
            map.put("hearingroom", spot.getHearingroom());
        if (spot.getHearinghandicapetc() != null && !spot.getHearinghandicapetc().isBlank())
            map.put("hearinghandicapetc", spot.getHearinghandicapetc());
        if (spot.getBabysparechair() != null && !spot.getBabysparechair().isBlank())
            map.put("babysparechair", spot.getBabysparechair());
        if (spot.getInfantsfamilyetc() != null && !spot.getInfantsfamilyetc().isBlank())
            map.put("infantsfamilyetc", spot.getInfantsfamilyetc());

        return map;
    }

    public Page<TourSpotSearchResponse> searchByTitle(String keyword, Pageable pageable, String sortBy) {
        if (keyword == null || keyword.trim().isBlank()) return Page.empty();
        if (!keyword.matches(".*[가-힣]+.*")) return Page.empty();

        Page<TourSpot> result = tourSpotRepository.findByTitleContaining(keyword, Pageable.unpaged());

        List<TourSpotSearchResponse> responses = result.getContent().stream()
                .map(spot -> {
                    long likeCount = tourSpotLikeRepository.countByTourSpotId(spot.getId());
                    return TourSpotSearchResponse.from(spot, likeCount);
                })
                .toList();

        if ("likes".equals(sortBy)) {
            responses = responses.stream()
                    .sorted(Comparator.comparingLong(TourSpotSearchResponse::getLikeCount).reversed())
                    .toList();
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), responses.size());
        List<TourSpotSearchResponse> pageContent = responses.subList(start, end);

        return new PageImpl<>(pageContent, pageable, responses.size());
    }

    public String getFirstImageByTourSpotId(Long tourSpotId) {
        return tourSpotRepository.findByContentId(tourSpotId)
                .map(TourSpot::getFirstImage)
                .orElse(null);
    }

    public TourSpot getTourSpotByContentId(Long contentId) {
        return tourSpotRepository.findByContentId(contentId)
                .orElse(null); // orElseThrow()를 써도 되지만 null-safe하게 처리하려면 이게 좋습니다
    }


}
