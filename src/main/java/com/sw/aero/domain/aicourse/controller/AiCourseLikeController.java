package com.sw.aero.domain.aicourse.controller;

import com.sw.aero.domain.aicourse.dto.AiCourseResponse;
import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.aicourse.repository.AiCourseRepository;
import com.sw.aero.domain.course.entity.CourseLike;
import com.sw.aero.domain.course.repository.CourseLikeRepository;
import com.sw.aero.domain.user.entity.User;
import com.sw.aero.domain.user.repository.UserRepository;
import com.sw.aero.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai-courses")
@RequiredArgsConstructor
public class AiCourseLikeController {

    private final AiCourseRepository aiCourseRepository;
    private final CourseLikeRepository courseLikeRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @PostMapping("/{courseId}/like")
    public void likeAiCourse(@PathVariable Long courseId,
                             @RequestHeader("Authorization") String accessToken) {
        Long userId = jwtProvider.getUserIdFromToken(accessToken.replace("Bearer ", ""));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        AiCourse course = aiCourseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        if (courseLikeRepository.findByUserAndAiCourse(user, course).isEmpty()) {
            CourseLike like = CourseLike.builder()
                    .user(user)
                    .aiCourse(course)
                    .build();
            courseLikeRepository.save(like);
        }
    }

    @DeleteMapping("/{courseId}/like")
    public void unlikeAiCourse(@PathVariable Long courseId,
                               @RequestHeader("Authorization") String accessToken) {
        Long userId = jwtProvider.getUserIdFromToken(accessToken.replace("Bearer ", ""));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        AiCourse course = aiCourseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        courseLikeRepository.findByUserAndAiCourse(user, course)
                .ifPresent(courseLikeRepository::delete);
    }

    @GetMapping("/likes")
    public List<AiCourseResponse> getLikedAiCourses(@RequestHeader("Authorization") String accessToken) {
        Long userId = jwtProvider.getUserIdFromToken(accessToken.replace("Bearer ", ""));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<CourseLike> likes = courseLikeRepository.findAllByUserAndAiCourseIsNotNull(user);

        return likes.stream()
                .map(like -> {
                    AiCourse course = like.getAiCourse();
                    long likeCount = courseLikeRepository.countByAiCourse(course);
                    return AiCourseResponse.from(course, likeCount);
                })
                .toList();
    }
}
