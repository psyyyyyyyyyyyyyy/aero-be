package com.sw.aero.domain.course.dto.response;

import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.course.entity.UserCourse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CombinedCourseResponse {
    private Long id;
    private String type;
    private String title;
    private String theme;
    private String startDate;
    private String endDate;
    private String people;
    private boolean allow;
    private long likeCount;
    private List<String> barrierFreeKeys;
    private LocalDateTime createdAt;

    public static CombinedCourseResponse fromUserCourse(UserCourse course, long likeCount) {
        return CombinedCourseResponse.builder()
                .id(course.getId())
                .type("user")
                .title(course.getTitle())
                .theme(course.getTheme())
                .startDate(course.getStartDate().toString())
                .endDate(course.getEndDate().toString())
                .people(course.getPeople())
                .allow(course.isAllow())
                .likeCount(likeCount)
                .createdAt(course.getCreatedAt())
                .barrierFreeKeys(List.of())
                .build();
    }

    public static CombinedCourseResponse fromAiCourse(AiCourse course, long likeCount) {
        List<String> allBarrierKeys = course.getSchedules().stream()
                .flatMap(detail -> detail.getBarrierFree().keySet().stream())
                .distinct()
                .toList();

        return CombinedCourseResponse.builder()
                .id(course.getId())
                .type("ai")
                .title(course.getTitle())
                .theme(course.getTheme())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .people(course.getPeople())
                .allow(course.isAllow())
                .likeCount(likeCount)
                .createdAt(course.getCreatedAt())
                .barrierFreeKeys(allBarrierKeys)
                .build();
    }
}
