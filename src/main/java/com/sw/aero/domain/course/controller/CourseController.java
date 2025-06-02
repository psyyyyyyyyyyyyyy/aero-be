package com.sw.aero.domain.course.controller;

import com.sw.aero.domain.course.dto.request.CourseRequest;
import com.sw.aero.domain.course.dto.response.CourseResponse;
import com.sw.aero.domain.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/{userId}")
    public Long createCourse(@PathVariable Long userId, @RequestBody CourseRequest request) {
        return courseService.createCourse(userId, request);
    }

    @GetMapping
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{courseId}")
    public CourseResponse getCourse(@PathVariable Long courseId) {
        return courseService.getCourseById(courseId);
    }

    @PutMapping("/{courseId}")
    public CourseResponse updateCourse(@PathVariable Long courseId, @RequestBody CourseRequest request) {
        return courseService.updateCourse(courseId, request);
    }

    @DeleteMapping("/{courseId}")
    public void deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
    }

    @GetMapping("/users/{userId}/courses")
    public List<CourseResponse> getCoursesByUser(@PathVariable Long userId) {
        return courseService.getCoursesByUser(userId);
    }

}
