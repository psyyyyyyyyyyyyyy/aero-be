package com.sw.aero.domain.aicourse.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiDetailSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int day;
    private String time;
    private String place;
    private String placeId;
    private String address;
    @Column(length = 1000)
    private String imageUrl;
    private String description;

    @ElementCollection
    @CollectionTable(name = "barrier_free_info", joinColumns = @JoinColumn(name = "detail_id"))
    @MapKeyColumn(name = "type")
    @Column(name = "description")
    private Map<String, String> barrierFree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_course_id")
    @JsonBackReference  // 직렬화 시 무시하여 무한 순환 방지
    private AiCourse aiCourse;
}