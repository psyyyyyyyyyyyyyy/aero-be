package com.sw.aero.domain.tourspot.controller;

import com.sw.aero.domain.tourspot.dto.TourSpotResponse;
import com.sw.aero.domain.tourspot.dto.TourSpotSearchResponse;
import com.sw.aero.domain.tourspot.entity.TourSpot;
import com.sw.aero.domain.tourspot.repository.TourSpotRepository;
import com.sw.aero.domain.tourspot.service.TourSpotService;
import com.sw.aero.domain.user.entity.User;
import com.sw.aero.domain.user.repository.UserRepository;
import com.sw.aero.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tourspots")
@RequiredArgsConstructor
public class TourSpotController {

    private final TourSpotService tourSpotService;
    private final TourSpotRepository tourSpotRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @PostMapping("/import")
    public String importTourSpots(@RequestParam List<String> regionCodes) {
        tourSpotService.importBarrierFreeTourSpots(regionCodes);
        return "관광지 저장 완료!";
    }

    // 전체 관광지 목록 조회 (테스트용)
    @GetMapping
    public List<TourSpot> getAllTourSpots() {
        return tourSpotRepository.findAll();
    }

    // 조건 필터 적용 + 페이징 + 정렬 관광지 조회
    @GetMapping("/filter")
    public Page<TourSpotResponse> getFilteredTourSpots(
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) String sigunguCode,
            @RequestParam(required = false) List<String> facilityFilters,
            @RequestParam(required = false) List<String> themeFilters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recent") String sortBy,
            @RequestHeader(value = "Authorization", required = false) String accessToken // ✅ null 허용
    ) {
        User user = null;

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            try {
                Long userId = jwtProvider.getUserIdFromToken(accessToken.replace("Bearer ", ""));
                user = userRepository.findById(userId).orElse(null); // 없으면 null 유지
            } catch (Exception e) {
                user = null; // 잘못된 토큰이면 무시
            }
        }

        Sort sort = Sort.by("id").descending();
        if ("recent".equals(sortBy)) {
            sort = Sort.by("createdTime").descending();
        } else if ("likes".equals(sortBy)) {
            sort = Sort.unsorted(); // likes는 수동 정렬
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return tourSpotService.getFilteredTourSpots(
                areaCode, sigunguCode, facilityFilters, themeFilters, pageable, user, sortBy
        );
    }


    @GetMapping("/search")
    public Page<TourSpotSearchResponse> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recent") String sortBy
    ) {
        Sort sort = Sort.by("createdTime").descending(); // 기본
        if ("likes".equals(sortBy)) {
            sort = Sort.by("likeCount").descending(); // likeCount 기준 정렬
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return tourSpotService.searchByTitle(keyword, pageable, sortBy);
    }
}