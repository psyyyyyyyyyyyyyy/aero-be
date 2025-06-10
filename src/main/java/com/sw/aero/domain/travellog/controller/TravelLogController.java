package com.sw.aero.domain.travellog.controller;

import com.sw.aero.domain.travellog.entity.TravelLog;
import com.sw.aero.domain.travellog.service.TravelLogService;
import com.sw.aero.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travel-logs")
public class TravelLogController {

    private final TravelLogService service;
    private final JwtProvider jwtProvider;

    @GetMapping
    public List<TravelLog> getMyTravelLogs(@RequestHeader("Authorization") String accessToken) {
        Long userId = jwtProvider.getUserIdFromToken(accessToken.replace("Bearer ", ""));
        return service.getMyTravelLogs(userId);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TravelLog> saveTravelLog(
            @RequestParam String address,
            @RequestParam String content,
            @RequestPart MultipartFile image,
            @RequestHeader("Authorization") String accessToken
    ) throws Exception {
        Long userId = jwtProvider.getUserIdFromToken(accessToken.replace("Bearer ", ""));
        return ResponseEntity.ok(service.save(userId, address, content, image));
    }

}
