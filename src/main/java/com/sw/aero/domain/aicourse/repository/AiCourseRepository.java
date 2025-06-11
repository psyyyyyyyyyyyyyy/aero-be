package com.sw.aero.domain.aicourse.repository;

import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiCourseRepository extends JpaRepository<AiCourse, Long> {
    List<AiCourse> findAllByUser(User user);

    void deleteAllByUser(User user);
}
