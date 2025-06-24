package com.sw.aero.domain.aicourse.dto;

import com.sw.aero.domain.aicourse.entity.AiCourse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
public class AiCourseDetailResponse {
    private Long id;
    private String title;
    private String theme;
    private String startDate;
    private String endDate;
    private String people;
    private boolean allow;
    private long likeCount;
    private boolean liked;
    private LocalDateTime createdAt;
    private UserInfo user;
    private List<ScheduleInfo> schedules;

    @Data
    @Builder
    public static class UserInfo {
        private Long id;
        private String socialId;
        private String name;
        private String email;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    public static class ScheduleInfo {
        private Long id;
        private int day;
        private String time;
        private String place;
        private String placeId;
        private String address;
        private Double mapX;
        private Double mapY;
        private String imageUrl;
        private String description;
        private Map<String, String> barrierFree;
    }

    public static AiCourseDetailResponse from(AiCourse course, long likeCount, boolean liked) {
        return AiCourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .theme(course.getTheme())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .people(course.getPeople())
                .allow(course.isAllow())
                .createdAt(course.getCreatedAt())
                .liked(liked)
                .likeCount(likeCount)
                .user(UserInfo.builder()
                        .id(course.getUser().getId())
                        .socialId(course.getUser().getSocialId())
                        .name(course.getUser().getName())
                        .email(course.getUser().getEmail())
                        .createdAt(course.getUser().getCreatedAt())
                        .build())
                .schedules(course.getSchedules().stream()
                        .map(s -> ScheduleInfo.builder()
                                .id(s.getId())
                                .day(s.getDay())
                                .time(s.getTime())
                                .place(s.getPlace())
                                .placeId(s.getPlaceId())
                                .address(s.getAddress())
                                .mapX(s.getMapX())
                                .mapY(s.getMapY())
                                .imageUrl(s.getImageUrl())
                                .description(s.getDescription())
                                .barrierFree(s.getBarrierFree())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
