package com.sw.aero.domain.aicourse.dto;

import lombok.Data;

@Data
public class AiTravelRequestDto {
    private String region;      // 서울, 부산 등
    private String people;      // 혼자서, 친구랑, 연인과, 가족과
    private String startDate;   // 2025-08-01
    private String endDate;     // 2025-08-03
    private String theme;       // 음식, 자연관광 등
    private String pace;        // 빡빡하게 or 널널하게
}
