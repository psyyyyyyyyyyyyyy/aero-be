package com.sw.aero.global.s3.controller;

import com.sw.aero.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3UploadController {

    private final S3Service uploader;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@RequestPart("file") MultipartFile file) {
        try {
            String fileUrl = uploader.upload(file, "gallery");
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("업로드 실패: " + e.getMessage());
        }
    }
}
