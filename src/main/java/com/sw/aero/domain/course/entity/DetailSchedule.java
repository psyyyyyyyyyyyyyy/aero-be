package com.sw.aero.domain.course.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DetailSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int day;
    private String time;
    private String place;
    private String address;
    private Double mapX;
    private Double mapY;
    private String description;
    private String firstImage;
    private Long tourSpotId;  // 관광지 ID

    @ManyToOne(fetch = FetchType.LAZY)
    private UserCourse userCourse;
}