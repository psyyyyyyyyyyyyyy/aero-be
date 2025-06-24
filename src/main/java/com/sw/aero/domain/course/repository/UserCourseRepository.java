package com.sw.aero.domain.course.repository;

import com.sw.aero.domain.course.entity.UserCourse;
import com.sw.aero.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    Page<UserCourse> findByUser(User user, Pageable pageable);

    void deleteAllByUser(User user);

}
