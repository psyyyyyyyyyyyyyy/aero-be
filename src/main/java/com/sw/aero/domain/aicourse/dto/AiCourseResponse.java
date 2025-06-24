package com.sw.aero.domain.aicourse.dto;

import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.aicourse.entity.AiDetailSchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AiCourseResponse {

    private Long id;
    private String title;
    private String theme;
    private String startDate;
    private String endDate;
    private String people;
    private boolean allow;
    private long likeCount;
    private LocalDateTime createdAt;
    private Long userId;
    private String image;

    public static AiCourseResponse from(AiCourse course, long likeCount) {
        String image = course.getSchedules().stream()
                .map(AiDetailSchedule::getImageUrl)
                .filter(img -> img != null && !img.isBlank())
                .findFirst()
                .orElse(null);

        return AiCourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .theme(course.getTheme())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .people(course.getPeople())
                .allow(course.isAllow())
                .likeCount(likeCount)
                .createdAt(course.getCreatedAt())
                .image(image)
                .build();
    }
}
