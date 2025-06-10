package com.sw.aero.domain.aicourse.dto;

import com.sw.aero.domain.aicourse.entity.AiCourse;
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

    public static AiCourseResponse from(AiCourse course, long likeCount) {
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
                .userId(course.getUser() != null ? course.getUser().getId() : null)
                .build();
    }
}
