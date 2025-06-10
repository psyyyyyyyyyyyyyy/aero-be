package com.sw.aero.domain.course.service;

import com.sw.aero.domain.course.dto.request.CourseRequest;
import com.sw.aero.domain.course.dto.response.CourseResponse;
import com.sw.aero.domain.course.entity.CourseLike;
import com.sw.aero.domain.course.entity.DetailSchedule;
import com.sw.aero.domain.course.entity.UserCourse;
import com.sw.aero.domain.course.repository.CourseLikeRepository;
import com.sw.aero.domain.course.repository.DetailScheduleRepository;
import com.sw.aero.domain.course.repository.UserCourseRepository;
import com.sw.aero.domain.tourspot.service.TourSpotService;
import com.sw.aero.domain.user.entity.User;
import com.sw.aero.domain.user.repository.UserRepository;
import com.sw.aero.global.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final UserCourseRepository courseRepository;
    private final UserRepository userRepository;
    private final DetailScheduleRepository detailScheduleRepository;
    private final CourseLikeRepository courseLikeRepository;
    private final TourSpotService tourSpotService;


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
                        .tourSpotId(d.getTourSpotId())
                        .userCourse(course)
                        .build())
                .collect(Collectors.toList());

        detailScheduleRepository.saveAll(detailSchedules);

        return course.getId();
    }

    public CourseResponse getCourseById(Long courseId) {
        UserCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 코스를 찾을 수 없습니다: " + courseId));

        long likeCount = courseLikeRepository.countByUserCourse(course);

        List<CourseResponse.DetailScheduleDto> schedules = course.getDetailedSchedule().stream()
                .map(detail -> {
                    Map<String, String> barrierFree = null;
                    if (detail.getTourSpotId() != null) {
                        barrierFree = tourSpotService.getBarrierFreeInfoByTourSpotId(detail.getTourSpotId());
                    }
                    return CourseResponse.DetailScheduleDto.from(detail, barrierFree);
                })
                .toList();

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .theme(course.getTheme())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .people(course.getPeople())
                .allow(course.isAllow())
                .likeCount(likeCount)
                .detailedSchedule(schedules)
                .build();
    }

    public List<CourseResponse> getAllCourses() {
        List<UserCourse> userCourses = courseRepository.findAll(); // 리스트 먼저 가져오기
        return userCourses.stream()
                .map(course -> {
                    long likeCount = courseLikeRepository.countByUserCourse(course);
                    return CourseResponse.from(course, likeCount);
                })
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
                        .tourSpotId(d.getTourSpotId())
                        .userCourse(course)
                        .build())
                .toList();

        detailScheduleRepository.saveAll(newSchedules);

        long likeCount = courseLikeRepository.countByUserCourse(course);
        return CourseResponse.from(course, likeCount);

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
                .map(course -> {
                    long likeCount = courseLikeRepository.countByUserCourse(course);
                    return CourseResponse.from(course, likeCount);
                })
                .toList();

    }

    public void likeCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다: " + userId));

        UserCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("해당 코스를 찾을 수 없습니다: " + courseId));


        if (courseLikeRepository.findByUserAndUserCourse(user, course).isEmpty()) {
            courseLikeRepository.save(CourseLike.builder().user(user).userCourse(course).build());
        }
    }

    public void unlikeCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다: " + userId));

        UserCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("해당 코스를 찾을 수 없습니다: " + courseId));

        courseLikeRepository.findByUserAndUserCourse(user, course)
                .ifPresent(courseLikeRepository::delete);
    }

    public List<CourseResponse> getLikedCourses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다: " + userId));

        return courseLikeRepository.findAllByUser(user).stream()
                .filter(like -> like.getUserCourse() != null)
                .map(like -> {
                    UserCourse course = like.getUserCourse();
                    long likeCount = courseLikeRepository.countByUserCourse(course);
                    return CourseResponse.from(course, likeCount);
                })
                .toList();
    }


}
