package com.sw.aero.domain.course.service;

import com.sw.aero.domain.course.dto.request.CourseRequest;
import com.sw.aero.domain.course.entity.DetailSchedule;
import com.sw.aero.domain.course.entity.UserCourse;
import com.sw.aero.domain.course.repository.DetailScheduleRepository;
import com.sw.aero.domain.course.repository.UserCourseRepository;
import com.sw.aero.domain.user.entity.User;
import com.sw.aero.domain.user.repository.UserRepository;
import com.sw.aero.domain.course.dto.response.CourseResponse;
import com.sw.aero.global.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final UserCourseRepository courseRepository;
    private final UserRepository userRepository;
    private final DetailScheduleRepository detailScheduleRepository;

    public Long createCourse(Long userId, CourseRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserCourse course = UserCourse.builder()
                .user(user)
                .title(request.getTitle())
                .theme(request.getTheme())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .people(request.getPeople())
                .allow(request.isAllow())
                .build();

        courseRepository.save(course);

        List<DetailSchedule> detailSchedules = request.getDetailedSchedule().stream()
                .map(d -> DetailSchedule.builder()
                        .time(d.getTime())
                        .place(d.getPlace())
                        .description(d.getDescription())
                        .userCourse(course)
                        .build())
                .collect(Collectors.toList());

        detailScheduleRepository.saveAll(detailSchedules);

        return course.getId();
    }

    public CourseResponse getCourseById(Long courseId) {
        UserCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 코스를 찾을 수 없습니다: " + courseId));

        return CourseResponse.from(course);
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseResponse::from)
                .toList();
    }

    public CourseResponse updateCourse(Long courseId, CourseRequest request) {
        UserCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 코스를 찾을 수 없습니다: " + courseId));

        course.update(request.getTitle(), request.getTheme(), request.getStartDate(), request.getEndDate(),
                request.getPeople(), request.isAllow());

        detailScheduleRepository.deleteAllByUserCourse(course);

        List<DetailSchedule> newSchedules = request.getDetailedSchedule().stream()
                .map(d -> DetailSchedule.builder()
                        .time(d.getTime())
                        .place(d.getPlace())
                        .description(d.getDescription())
                        .userCourse(course)
                        .build())
                .toList();

        detailScheduleRepository.saveAll(newSchedules);

        return CourseResponse.from(course);
    }

    public void deleteCourse(Long courseId) {
        UserCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 코스를 찾을 수 없습니다: " + courseId));

        detailScheduleRepository.deleteAllByUserCourse(course);
        courseRepository.delete(course);
    }

    public List<CourseResponse> getCoursesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 유저를 찾을 수 없습니다: " + userId));

        List<UserCourse> userCourses = courseRepository.findByUser(user);
        return userCourses.stream()
                .map(CourseResponse::from)
                .toList();
    }

}
