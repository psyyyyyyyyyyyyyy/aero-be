package com.sw.aero.domain.travellog.service;

import com.sw.aero.domain.travellog.entity.TravelLog;
import com.sw.aero.domain.travellog.repository.TravelLogRepository;
import com.sw.aero.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelLogService {

    private final TravelLogRepository repository;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.directory}")
    private String uploadDir;

    public TravelLog save(String address, String content, MultipartFile file) throws IOException {
        // S3 업로드
        String imageUrl = s3Service.upload(file, uploadDir);

        // S3 URL 저장
        TravelLog log = TravelLog.builder()
                .address(address)
                .content(content)
                .imageUrl(imageUrl)
                .build();

        return repository.save(log);
    }

    public List<TravelLog> getAll() {
        return repository.findAll();
    }
}
