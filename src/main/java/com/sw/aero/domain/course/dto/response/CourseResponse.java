package com.sw.aero.domain.course.dto.response;

import com.sw.aero.domain.course.entity.DetailSchedule;
import com.sw.aero.domain.course.entity.UserCourse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class CourseResponse {
    private Long id;
    private String title;
    private String theme;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String people;
    private boolean allow;

    private List<DetailScheduleDto> detailedSchedule;
    private long likeCount;

    //  기본 from(): barrierFree 정보 없이 변환 (getAllCourses, update 등에서 사용)
    public static CourseResponse from(UserCourse course, long likeCount) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .theme(course.getTheme())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .people(course.getPeople())
                .allow(course.isAllow())
                .likeCount(likeCount)
                .detailedSchedule(course.getDetailedSchedule().stream()
                        .map(DetailScheduleDto::from)
                        .toList())
                .build();
    }

    @Getter
    @Builder
    public static class DetailScheduleDto {
        private String time;
        private String place;
        private String description;
        private Long tourSpotId;
        private Map<String, String> barrierFree;

        // barrierFree 포함해서 변환 (getCourseById에서 사용)
        public static DetailScheduleDto from(DetailSchedule entity, Map<String, String> barrierFree) {
            return DetailScheduleDto.builder()
                    .time(entity.getTime())
                    .place(entity.getPlace())
                    .description(entity.getDescription())
                    .tourSpotId(entity.getTourSpotId())
                    .barrierFree(barrierFree)
                    .build();
        }

        // barrierFree 없이 변환 (from(UserCourse ...)에서 사용)
        public static DetailScheduleDto from(DetailSchedule entity) {
            return DetailScheduleDto.builder()
                    .time(entity.getTime())
                    .place(entity.getPlace())
                    .description(entity.getDescription())
                    .tourSpotId(entity.getTourSpotId())
                    .barrierFree(null)
                    .build();
        }
    }
}
