package com.sw.aero.domain.course.repository;

import com.sw.aero.domain.course.entity.UserCourse;
import com.sw.aero.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    List<UserCourse> findByUser(User user);

    void deleteAllByUser(User user);

}
