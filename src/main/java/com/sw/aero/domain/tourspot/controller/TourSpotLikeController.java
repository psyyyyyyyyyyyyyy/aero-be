package com.sw.aero.domain.tourspot.controller;

import com.sw.aero.domain.tourspot.dto.TourSpotResponse;
import com.sw.aero.domain.tourspot.service.TourSpotLikeService;
import com.sw.aero.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tourspots/likes")
public class TourSpotLikeController {

    private final TourSpotLikeService likeService;
    private final JwtProvider jwtProvider;

    @PostMapping("/{tourSpotId}")
    public void like(
            @PathVariable Long tourSpotId,
            @RequestHeader("Authorization") String accessToken
    ) {
        Long userId = jwtProvider.getUserIdFromToken(accessToken.replace("Bearer ", ""));
        likeService.likeSpot(userId, tourSpotId);
    }

    @DeleteMapping("/{tourSpotId}")
    public void unlike(
            @PathVariable Long tourSpotId,
            @RequestHeader("Authorization") String accessToken
    ) {
        Long userId = jwtProvider.getUserIdFromToken(accessToken.replace("Bearer ", ""));
        likeService.unlikeSpot(userId, tourSpotId);
    }

    @GetMapping("/{tourSpotId}")
    public Map<String, Object> getLikeInfo(
            @PathVariable Long tourSpotId,
            @RequestHeader("Authorization") String accessToken
    ) {
        Long userId = jwtProvider.getUserIdFromToken(accessToken.replace("Bearer ", ""));
        boolean liked = likeService.hasLiked(userId, tourSpotId);
        long count = likeService.countLikes(tourSpotId);
        return Map.of("liked", liked, "likeCount", count);
    }

    @GetMapping("/my")
    public Page<TourSpotResponse> getLikedTourSpots(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = jwtProvider.getUserIdFromToken(accessToken.replace("Bearer ", ""));
        Pageable pageable = PageRequest.of(page, size);
        return likeService.getLikedTourSpots(userId, pageable);
    }
}
