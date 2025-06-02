package com.sw.aero.domain.tourspot.controller;

import com.sw.aero.domain.tourspot.service.TourSpotLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tourspots/likes")
public class TourSpotLikeController {

    private final TourSpotLikeService likeService;

    @PostMapping("/{tourSpotId}")
    public void like(@RequestParam Long userId, @PathVariable Long tourSpotId) {
        likeService.likeSpot(userId, tourSpotId);
    }

    @DeleteMapping("/{tourSpotId}")
    public void unlike(@RequestParam Long userId, @PathVariable Long tourSpotId) {
        likeService.unlikeSpot(userId, tourSpotId);
    }

    @GetMapping("/{tourSpotId}")
    public Map<String, Object> getLikeInfo(@RequestParam Long userId, @PathVariable Long tourSpotId) {
        boolean liked = likeService.hasLiked(userId, tourSpotId);
        long count = likeService.countLikes(tourSpotId);
        return Map.of("liked", liked, "likeCount", count);
    }
}
