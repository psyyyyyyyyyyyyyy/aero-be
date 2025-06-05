package com.sw.aero.domain.course.dto.response;

import com.sw.aero.domain.course.entity.DetailSchedule;
import com.sw.aero.domain.course.entity.UserCourse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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

    public static CourseResponse from(UserCourse course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .theme(course.getTheme())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .people(course.getPeople())
                .allow(course.isAllow())
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

        public static DetailScheduleDto from(DetailSchedule entity) {
            return DetailScheduleDto.builder()
                    .time(entity.getTime())
                    .place(entity.getPlace())
                    .description(entity.getDescription())
                    .build();
        }
    }
}
