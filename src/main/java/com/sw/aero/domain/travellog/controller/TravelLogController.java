package com.sw.aero.domain.travellog.controller;

import com.sw.aero.domain.travellog.entity.TravelLog;
import com.sw.aero.domain.travellog.service.TravelLogService;
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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TravelLog> saveTravelLog(
            @RequestParam String address,
            @RequestParam String content,
            @RequestPart MultipartFile image
    ) throws Exception {
        return ResponseEntity.ok(service.save(address, content, image));
    }

    @GetMapping
    public List<TravelLog> getAllTravelLogs() {
        return service.getAll();
    }
}
