package com.sw.aero.domain.tourspot.controller;

import com.sw.aero.domain.tourspot.dto.TourSpotResponse;
import com.sw.aero.domain.tourspot.dto.TourSpotSearchResponse;
import com.sw.aero.domain.tourspot.entity.TourSpot;
import com.sw.aero.domain.tourspot.repository.TourSpotRepository;
import com.sw.aero.domain.tourspot.service.TourSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tourspots")
@RequiredArgsConstructor
public class TourSpotController {

    private final TourSpotService tourSpotService;
    private final TourSpotRepository tourSpotRepository;

    @PostMapping("/import")
    public String importTourSpots() {
        tourSpotService.importBarrierFreeTourSpots();
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
            @RequestParam(defaultValue = "recent") String sortBy
    ) {
        Sort sort = Sort.by("id").descending(); // 기본
        if ("recent".equals(sortBy)) {
            sort = Sort.by("createdTime").descending();
        } else if ("likes".equals(sortBy)) {
            sort = Sort.unsorted(); // 수동 정렬 처리
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return tourSpotService.getFilteredTourSpots(areaCode, sigunguCode, facilityFilters, themeFilters, pageable);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TourSpotSearchResponse>> search(@RequestParam String keyword) {
        List<TourSpotSearchResponse> results = tourSpotService.searchByTitle(keyword);
        return ResponseEntity.ok(results);
    }
}