package com.sw.aero.domain.course.repository;

import com.sw.aero.domain.course.entity.DetailSchedule;
import com.sw.aero.domain.course.entity.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailScheduleRepository extends JpaRepository<DetailSchedule, Long> {
    void deleteAllByUserCourse(UserCourse course);
}
