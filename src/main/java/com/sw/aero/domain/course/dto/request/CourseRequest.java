package com.sw.aero.domain.course.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Schema(description = "코스 생성 요청 DTO")
public class CourseRequest {

    @Schema(example = "강릉 힐링여행")
    private String title;

    @Schema(example = "힐링")
    private String theme;

    @Schema(example = "2025-06-10T00:00:00")
    private LocalDateTime startDate;

    @Schema(example = "2025-06-12T00:00:00")
    private LocalDateTime endDate;

    @Schema(example = "혼자서")
    private String people;

    @Schema(example = "true")
    private boolean allow;

    @Schema(description = "상세 일정들", example = "[{\"time\":\"PM 2:00\",\"place\":\"○○○\",\"description\":\"세부 설명\"}]")
    private List<DetailScheduleRequest> detailedSchedule;

    @Getter
    public static class DetailScheduleRequest {
        private String time;
        private String place;
        private String description;
    }
}