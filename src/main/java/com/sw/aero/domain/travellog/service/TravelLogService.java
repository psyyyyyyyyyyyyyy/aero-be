package com.sw.aero.domain.travellog.service;

import com.sw.aero.domain.travellog.entity.TravelLog;
import com.sw.aero.domain.travellog.repository.TravelLogRepository;
import com.sw.aero.domain.user.entity.User;
import com.sw.aero.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.directory}")
    private String uploadDir;

    public TravelLog save(Long userId, String address, String content, MultipartFile file) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // S3 업로드
        String imageUrl = s3Service.upload(file, uploadDir);

        // S3 URL 저장
        TravelLog log = TravelLog.builder()
                .address(address)
                .content(content)
                .imageUrl(imageUrl)
                .user(user)
                .build();

        return repository.save(log);
    }

    public List<TravelLog> getMyTravelLogs(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return repository.findAllByUser(user);
    }
}
