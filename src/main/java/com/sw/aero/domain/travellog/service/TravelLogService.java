package com.sw.aero.domain.travellog.service;

import com.sw.aero.domain.travellog.entity.TravelLog;
import com.sw.aero.domain.travellog.repository.TravelLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TravelLogService {

    private final TravelLogRepository repository;

    @Value("${cloud.aws.s3.directory}")
    private String uploadDir;

    public TravelLog save(String address, String content, MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = uploadDir + fileName;

        // 실제 디렉토리에 파일 저장
        File dest = new File(filePath);
        file.transferTo(dest);

        TravelLog log = TravelLog.builder()
                .address(address)
                .content(content)
                .imageUrl("/uploads/" + fileName)
                .build();

        return repository.save(log);
    }

    public List<TravelLog> getAll() {
        return repository.findAll();
    }
}
