package com.sw.aero.domain.aicourse.repository;

import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiCourseRepository extends JpaRepository<AiCourse, Long> {
    Page<AiCourse> findAllByUser(User user, Pageable pageable);

    void deleteAllByUser(User user);
}
