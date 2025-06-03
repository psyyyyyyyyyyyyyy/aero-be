package com.sw.aero.global.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * MultipartFile 업로드 → S3 URL 반환
     */
    public String upload(MultipartFile multipartFile, String dirName) {
        String originalFileName = multipartFile.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID() + "_" + originalFileName.replaceAll("\\s", "_");
        String key = dirName + "/" + uniqueFileName;

        try {
            File tempFile = convertToTempFile(multipartFile);
            s3Client.putObject(new PutObjectRequest(bucketName, key, tempFile));
            tempFile.delete(); // 삭제 실패해도 무시
            return s3Client.getUrl(bucketName, key).toString();
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 기존 파일 삭제 후 새 파일 업로드 → 새 URL 반환
     */
    public String updateFile(MultipartFile newFile, String oldKey, String dirName) {
        deleteFile(oldKey);
        return upload(newFile, dirName);
    }

    /**
     * S3 객체 삭제
     */
    public void deleteFile(String key) {
        try {
            String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);
            s3Client.deleteObject(bucketName, decodedKey);
            log.info("S3에서 삭제 완료: {}", decodedKey);
        } catch (Exception e) {
            throw new RuntimeException("S3 파일 삭제 실패: " + e.getMessage(), e);
        }
    }

    /**
     * MultipartFile → 임시 File 변환
     */
    private File convertToTempFile(MultipartFile multipartFile) throws IOException {
        String prefix = "upload-";
        String suffix = multipartFile.getOriginalFilename()
                .substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        Path tempPath = Files.createTempFile(prefix, suffix);
        File tempFile = tempPath.toFile();
        multipartFile.transferTo(tempFile);
        return tempFile;
    }
}
