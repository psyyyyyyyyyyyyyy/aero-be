package com.sw.aero.domain.course.repository;

import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.course.entity.CourseLike;
import com.sw.aero.domain.course.entity.UserCourse;
import com.sw.aero.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseLikeRepository extends JpaRepository<CourseLike, Long> {
    Optional<CourseLike> findByUserAndUserCourse(User user, UserCourse course);

    List<CourseLike> findAllByUser(User user);

    long countByUserCourse(UserCourse course);

    // AI코스 좋아요 여부 확인
    Optional<CourseLike> findByUserAndAiCourse(User user, AiCourse aiCourse);

    // AI코스 좋아요 수
    long countByAiCourse(AiCourse aiCourse);

    // 유저가 좋아요 누른 AI 코스 목록
    List<CourseLike> findAllByUserAndAiCourseIsNotNull(User user);

    // 유저가 좋아요 누른 유저 코스 목록
    List<CourseLike> findAllByUserAndUserCourseIsNotNull(User user);

}
