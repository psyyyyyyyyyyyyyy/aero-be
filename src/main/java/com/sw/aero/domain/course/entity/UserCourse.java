package com.sw.aero.domain.course.entity;

import com.sw.aero.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String theme;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String people;
    private boolean allow;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "userCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailSchedule> detailedSchedule;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String theme, LocalDateTime startDate, LocalDateTime endDate, String people, boolean allow) {
        this.title = title;
        this.theme = theme;
        this.startDate = startDate;
        this.endDate = endDate;
        this.people = people;
        this.allow = allow;
    }


}