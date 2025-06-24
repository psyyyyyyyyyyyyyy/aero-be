package com.sw.aero.domain.aicourse.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TravelPlanDTO {
    private String title;
    private String theme;
    private String startDate;
    private String endDate;
    private String people;
    private boolean allow;
    private List<ScheduleDTO> schedules;

    @Data
    public static class ScheduleDTO {
        private int day;
        private List<DetailDTO> details;
    }

    @Data
    public static class DetailDTO {
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
}
