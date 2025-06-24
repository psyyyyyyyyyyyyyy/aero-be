package com.sw.aero.domain.course.dto.response;

import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.course.entity.DetailSchedule;
import com.sw.aero.domain.course.entity.UserCourse;
import com.sw.aero.domain.tourspot.service.TourSpotService;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@Builder
public class CombinedCourseResponse {
    private Long id;
    private String type;
    private String title;
    private String imageUrl;
    private String theme;
    private String startDate;
    private String endDate;
    private String people;
    private boolean allow;
    private long likeCount;
    private List<String> barrierFreeKeys;
    private LocalDateTime createdAt;

    public static CombinedCourseResponse fromUserCourse(UserCourse course, long likeCount, TourSpotService tourSpotService) {
        String imageUrl = null;
        List<DetailSchedule> schedules = course.getDetailedSchedule();
        if (schedules != null && !schedules.isEmpty()) {
            Long tourSpotId = schedules.get(0).getTourSpotId(); // 첫 장소의 관광지 ID
            if (tourSpotId != null) {
                imageUrl = tourSpotService.getFirstImageByTourSpotId(tourSpotId);
            }
        }

        // 모든 schedule 내 barrierFree key들 추출
        List<String> barrierFreeKeys = course.getDetailedSchedule().stream()
                .filter(detail -> detail.getTourSpotId() != null)
                .flatMap(detail -> {
                    Map<String, String> bf = tourSpotService.getBarrierFreeInfoByTourSpotId(detail.getTourSpotId());
                    return bf != null ? bf.keySet().stream() : Stream.empty();
                })
                .distinct()
                .toList();

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
                .barrierFreeKeys(barrierFreeKeys)
                .imageUrl(imageUrl)
                .build();
    }

    public static CombinedCourseResponse fromAiCourse(AiCourse course, long likeCount) {
        List<String> allBarrierKeys = course.getSchedules().stream()
                .flatMap(detail -> detail.getBarrierFree().keySet().stream())
                .distinct()
                .toList();

        // 유효한 imageUrl을 가진 첫 장소 찾기
        String imageUrl = course.getSchedules().stream()
                .map(detail -> detail.getImageUrl())
                .filter(url -> url != null && !url.trim().isEmpty())
                .findFirst()
                .orElse("");

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
                .imageUrl(imageUrl)
                .build();
    }

}
