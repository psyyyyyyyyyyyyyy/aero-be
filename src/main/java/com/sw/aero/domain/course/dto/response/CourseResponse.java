package com.sw.aero.domain.course.dto.response;

import com.sw.aero.domain.course.entity.DetailSchedule;
import com.sw.aero.domain.course.entity.UserCourse;
import com.sw.aero.domain.tourspot.entity.TourSpot;
import com.sw.aero.domain.tourspot.service.TourSpotService;
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
    private boolean liked;

    // 기본 from(): barrierFree 정보 없이 변환
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

    // barrierFree 정보 포함해서 변환
    public static CourseResponse from(UserCourse course, long likeCount, TourSpotService tourSpotService) {
        List<DetailScheduleDto> schedules = course.getDetailedSchedule().stream()
                .map(detail -> {
                    Map<String, String> barrierFree = null;
                    TourSpot spot = null;
                    if (detail.getTourSpotId() != null) {
                        barrierFree = tourSpotService.getBarrierFreeInfoByTourSpotId(detail.getTourSpotId());
                        spot = tourSpotService.getTourSpotByContentId(detail.getTourSpotId());
                    }
                    return DetailScheduleDto.from(detail, barrierFree, spot);
                })
                .toList();

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .theme(course.getTheme())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .people(course.getPeople())
                .allow(course.isAllow())
                .likeCount(likeCount)
                .detailedSchedule(schedules)
                .build();
    }

    // liked 포함 버전
    public static CourseResponse from(UserCourse course, long likeCount, boolean liked, TourSpotService tourSpotService) {
        List<DetailScheduleDto> schedules = course.getDetailedSchedule().stream()
                .map(detail -> {
                    Map<String, String> barrierFree = null;
                    TourSpot spot = null;
                    if (detail.getTourSpotId() != null) {
                        barrierFree = tourSpotService.getBarrierFreeInfoByTourSpotId(detail.getTourSpotId());
                        spot = tourSpotService.getTourSpotByContentId(detail.getTourSpotId());
                    }
                    return DetailScheduleDto.from(detail, barrierFree, spot);
                })
                .toList();

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .theme(course.getTheme())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .people(course.getPeople())
                .allow(course.isAllow())
                .likeCount(likeCount)
                .liked(liked)
                .detailedSchedule(schedules)
                .build();
    }

    @Getter
    @Builder
    public static class DetailScheduleDto {
        private int day;
        private String time;
        private String place;
        private String address;
        private Double mapX;
        private Double mapY;
        private String description;
        private Long tourSpotId;
        private String firstImage;
        private Map<String, String> barrierFree;

        public static DetailScheduleDto from(DetailSchedule entity) {
            return DetailScheduleDto.builder()
                    .day(entity.getDay())
                    .time(entity.getTime())
                    .place(entity.getPlace())
                    .address(null)
                    .mapX(null)
                    .mapY(null)
                    .description(entity.getDescription())
                    .tourSpotId(entity.getTourSpotId())
                    .firstImage(entity.getFirstImage())
                    .barrierFree(null)
                    .build();
        }

        public static DetailScheduleDto from(DetailSchedule entity, Map<String, String> barrierFree, TourSpot spot) {
            return DetailScheduleDto.builder()
                    .day(entity.getDay())
                    .time(entity.getTime())
                    .place(entity.getPlace())
                    .address(entity.getAddress() != null ? entity.getAddress() : (spot != null ? spot.getAddress() : null))
                    .mapX(entity.getMapX() != null ? entity.getMapX() : (spot != null && spot.getMapX() != null ? Double.valueOf(spot.getMapX()) : null))
                    .mapY(entity.getMapY() != null ? entity.getMapY() : (spot != null && spot.getMapY() != null ? Double.valueOf(spot.getMapY()) : null))
                    .description(entity.getDescription())
                    .tourSpotId(entity.getTourSpotId())
                    .firstImage(entity.getFirstImage())
                    .barrierFree(barrierFree)
                    .build();
        }
    }
}
