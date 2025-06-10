package com.sw.aero.domain.aicourse.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sw.aero.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String theme;
    private boolean allow;
    private String startDate;
    private String endDate;
    private String people;

    @OneToMany(mappedBy = "aiCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AiDetailSchedule> schedules;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 사용자 정보 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
