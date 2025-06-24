package com.sw.aero.domain.course.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

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

    @Schema(description = "상세 일정들", example = "[{\"day\": 1, \"time\":\"PM 2:00\",\"place\":\"○○○\",\"description\":\"세부 설명\", \"tourSpotId\": 126482}]")
    private List<DetailScheduleRequest> detailedSchedule;

    @Getter
    @Setter
    @Schema(description = "상세 일정 DTO")
    public static class DetailScheduleRequest {

        @Schema(example = "1")
        private Integer day;

        @Schema(example = "PM 2:00")
        private String time;

        @Schema(example = "○○○")
        private String place;

        @Schema(example = "세부 설명")
        private String description;

        @JsonProperty("contentId") // ✅ 프론트에서 오는 contentId를 tourSpotId로 매핑
        @Schema(example = "126482", description = "관광지 contentId (/api/tourspots/search 응답에서 제공)")
        private Long tourSpotId;

        @Schema(example = "http://tong.visitkorea.or.kr/cms/resource/78/3482078_image2_1.jpg")
        private String firstImage;

    }
}