package com.sw.aero.domain.course.entity;

import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CourseLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserCourse userCourse;

    @ManyToOne(fetch = FetchType.LAZY)
    private AiCourse aiCourse;
}

