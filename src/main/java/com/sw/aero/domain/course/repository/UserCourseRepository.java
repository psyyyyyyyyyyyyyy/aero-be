package com.sw.aero.domain.course.repository;

import com.sw.aero.domain.course.entity.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sw.aero.domain.user.entity.User;
import java.util.List;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    List<UserCourse> findByUser(User user);
}
