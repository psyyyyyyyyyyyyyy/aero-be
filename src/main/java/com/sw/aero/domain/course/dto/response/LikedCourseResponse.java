package com.sw.aero.domain.course.dto.response;

import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.aicourse.entity.AiDetailSchedule;
import com.sw.aero.domain.course.entity.DetailSchedule;
import com.sw.aero.domain.course.entity.UserCourse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LikedCourseResponse {
    private Long id;
    private String type; // "user" or "ai"
    private String title;
    private String theme;
    private String startDate;
    private String endDate;
    private String people;
    private boolean allow;
    private long likeCount;
    private LocalDateTime createdAt;
    private String image;

    // UserCourse 전용 생성 메서드
    public static LikedCourseResponse fromUserCourse(UserCourse course, long likeCount) {

        String image = course.getDetailedSchedule().stream()
                .map(DetailSchedule::getFirstImage)
                .filter(img -> img != null && !img.isBlank())
                .findFirst()
                .orElse(null);

        return LikedCourseResponse.builder()
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
                .image(image)
                .build();
    }

    // AiCourse 전용 생성 메서드
    public static LikedCourseResponse fromAiCourse(AiCourse course, long likeCount) {

        String image = course.getSchedules().stream()
                .map(AiDetailSchedule::getImageUrl)
                .filter(img -> img != null && !img.isBlank())
                .findFirst()
                .orElse(null);

        return LikedCourseResponse.builder()
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
                .image(image)
                .build();
    }
}
