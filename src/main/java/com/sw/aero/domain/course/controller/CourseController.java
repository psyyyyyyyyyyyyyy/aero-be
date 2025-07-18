package com.sw.aero.domain.course.controller;

import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.aicourse.repository.AiCourseRepository;
import com.sw.aero.domain.course.dto.request.CourseRequest;
import com.sw.aero.domain.course.dto.response.CombinedCourseResponse;
import com.sw.aero.domain.course.dto.response.CourseResponse;
import com.sw.aero.domain.course.dto.response.LikedCourseResponse;
import com.sw.aero.domain.course.entity.UserCourse;
import com.sw.aero.domain.course.repository.CourseLikeRepository;
import com.sw.aero.domain.course.repository.UserCourseRepository;
import com.sw.aero.domain.course.service.CourseService;
import com.sw.aero.domain.tourspot.service.TourSpotService;
import com.sw.aero.domain.user.entity.User;
import com.sw.aero.domain.user.repository.UserRepository;
import com.sw.aero.global.exception.NotFoundException;
import com.sw.aero.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final TourSpotService tourSpotService;
    private final CourseLikeRepository courseLikeRepository;
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;
    private final AiCourseRepository aiCourseRepository;
    private final JwtProvider jwtProvider;

    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더가 유효하지 않습니다.");
        }
        String token = authHeader.substring(7);
        return jwtProvider.getUserIdFromToken(token);
    }

    @PostMapping
    public Long createCourse(@RequestHeader("Authorization") String authHeader, @RequestBody CourseRequest request) {
        Long userId = extractUserIdFromToken(authHeader);
        return courseService.createCourse(userId, request);
    }

//    @GetMapping
//    public List<CourseResponse> getAllCourses() {
//        return courseService.getAllCourses();
//    }

    @GetMapping("/{courseId}")
    public CourseResponse getCourse(
            @PathVariable Long courseId,
            @RequestHeader(value = "Authorization", required = false) String accessToken // 선택적
    ) {
        User user = null;

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            try {
                Long userId = jwtProvider.getUserIdFromToken(accessToken.replace("Bearer ", ""));
                user = userRepository.findById(userId).orElse(null);
            } catch (Exception e) {
                user = null; // 잘못된 토큰이면 무시
            }
        }

        return courseService.getCourseById(courseId, user); // user가 null일 수도 있음
    }


    @PutMapping("/{courseId}")
    public CourseResponse updateCourse(@PathVariable Long courseId, @RequestBody CourseRequest request) {
        return courseService.updateCourse(courseId, request);
    }

    @DeleteMapping("/{courseId}")
    public void deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
    }

    @GetMapping("/users/courses")
    public ResponseEntity<?> getCoursesByUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = extractUserIdFromToken(authHeader);
        PageRequest pageable = PageRequest.of(page, size);

        if (type.equalsIgnoreCase("user")) {
            return ResponseEntity.ok(courseService.getUserCoursesByUser(userId, pageable));
        } else if (type.equalsIgnoreCase("ai")) {
            return ResponseEntity.ok(courseService.getAiCoursesByUser(userId, pageable));
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("userCourses", courseService.getUserCoursesByUser(userId, pageable));
            result.put("aiCourses", courseService.getAiCoursesByUser(userId, pageable));
            return ResponseEntity.ok(result);
        }
    }


    @PostMapping("/{courseId}/like")
    public void likeCourse(@PathVariable Long courseId, @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        courseService.likeCourse(userId, courseId);
    }

    @DeleteMapping("/{courseId}/like")
    public void unlikeCourse(@PathVariable Long courseId, @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        courseService.unlikeCourse(userId, courseId);
    }

    @GetMapping("/users/likes")
    public List<CourseResponse> getLikedCourses(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        return courseService.getLikedCourses(userId);
    }

    @GetMapping("/users/likes/all")
    public Page<LikedCourseResponse> getAllLikedCourses(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "like") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = extractUserIdFromToken(authHeader);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다: " + userId));

        List<LikedCourseResponse> userCourses = courseLikeRepository.findAllByUserAndUserCourseIsNotNull(user).stream()
                .map(like -> {
                    UserCourse course = like.getUserCourse();
                    long likeCount = courseLikeRepository.countByUserCourse(course);
                    return LikedCourseResponse.fromUserCourse(course, likeCount);
                })
                .toList();

        List<LikedCourseResponse> aiCourses = courseLikeRepository.findAllByUserAndAiCourseIsNotNull(user).stream()
                .map(like -> {
                    AiCourse course = like.getAiCourse();
                    long likeCount = courseLikeRepository.countByAiCourse(course);
                    return LikedCourseResponse.fromAiCourse(course, likeCount);
                })
                .toList();

        List<LikedCourseResponse> combined = new ArrayList<>();
        combined.addAll(userCourses);
        combined.addAll(aiCourses);

        // 정렬
        if ("like".equals(sortBy)) {
            combined.sort(Comparator.comparingLong(LikedCourseResponse::getLikeCount).reversed());
        } else if ("recent".equals(sortBy)) {
            combined.sort(Comparator.comparing(LikedCourseResponse::getCreatedAt).reversed());
        }

        // 페이징
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), combined.size());
        List<LikedCourseResponse> pageContent = combined.subList(start, end);

        return new PageImpl<>(pageContent, pageable, combined.size());
    }


    @GetMapping("/all")
    public Page<CombinedCourseResponse> getAllCourses(
            @RequestParam(required = false) List<String> theme,
            @RequestParam(required = false) List<String> barrierFree,
            @RequestParam(required = false) String type, // "user", "ai", or null
            @RequestParam(defaultValue = "recent") String sortBy, // "like", "recent"
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<CombinedCourseResponse> results = new ArrayList<>();

        // 유저 생성 코스 처리
        if (type == null || type.equals("user")) {
            List<UserCourse> userCourses = userCourseRepository.findAll();
            results.addAll(userCourses.stream()
                    .filter(UserCourse::isAllow)
                    .filter(course -> theme == null || theme.isEmpty() || theme.contains(course.getTheme()))
                    .map(course -> {
                        long likeCount = courseLikeRepository.countByUserCourse(course);
                        return CombinedCourseResponse.fromUserCourse(course, likeCount, tourSpotService);
                    })
                    .filter(dto -> barrierFree == null || barrierFree.isEmpty() || barrierFree.stream().allMatch(dto.getBarrierFreeKeys()::contains))
                    .toList());
        }

        // AI 생성 코스 처리
        if (type == null || type.equals("ai")) {
            List<AiCourse> aiCourses = aiCourseRepository.findAll();
            results.addAll(aiCourses.stream()
                    .filter(course -> theme == null || theme.isEmpty() || theme.contains(course.getTheme()))
                    .map(course -> {
                        long likeCount = courseLikeRepository.countByAiCourse(course);
                        return CombinedCourseResponse.fromAiCourse(course, likeCount);
                    })
                    .filter(dto -> barrierFree == null || barrierFree.isEmpty() || barrierFree.stream().allMatch(dto.getBarrierFreeKeys()::contains))
                    .toList());
        }

        // 정렬
        if ("like".equals(sortBy)) {
            results.sort(Comparator.comparingLong(CombinedCourseResponse::getLikeCount).reversed());
        } else if ("recent".equals(sortBy)) {
            results.sort(Comparator.comparing(CombinedCourseResponse::getCreatedAt).reversed());
        }

        // 페이징
        int start = Math.min(page * size, results.size());
        int end = Math.min(start + size, results.size());
        List<CombinedCourseResponse> paged = results.subList(start, end);

        return new PageImpl<>(paged, PageRequest.of(page, size), results.size());
    }

    @GetMapping("/search")
    public Page<CombinedCourseResponse> searchCourses(
            @RequestParam String keyword,
            @RequestParam(required = false) String type, // "user", "ai", or null
            @RequestParam(defaultValue = "recent") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<CombinedCourseResponse> results = new ArrayList<>();

        if (type == null || type.equals("user")) {
            List<UserCourse> userCourses = userCourseRepository.findAll();
            results.addAll(userCourses.stream()
                    .filter(course -> course.getTitle() != null && course.getTitle().contains(keyword))
                    .map(course -> {
                        long likeCount = courseLikeRepository.countByUserCourse(course);
                        return CombinedCourseResponse.fromUserCourse(course, likeCount, tourSpotService);
                    })
                    .toList());
        }

        if (type == null || type.equals("ai")) {
            List<AiCourse> aiCourses = aiCourseRepository.findAll();
            results.addAll(aiCourses.stream()
                    .filter(course -> course.getTitle() != null && course.getTitle().contains(keyword))
                    .map(course -> {
                        long likeCount = courseLikeRepository.countByAiCourse(course);
                        return CombinedCourseResponse.fromAiCourse(course, likeCount);
                    })
                    .toList());
        }

        // 정렬
        if ("like".equals(sortBy)) {
            results.sort(Comparator.comparingLong(CombinedCourseResponse::getLikeCount).reversed());
        } else if ("recent".equals(sortBy)) {
            results.sort(Comparator.comparing(CombinedCourseResponse::getCreatedAt).reversed());
        }

        // 페이징
        int start = Math.min(page * size, results.size());
        int end = Math.min(start + size, results.size());
        List<CombinedCourseResponse> paged = results.subList(start, end);

        return new PageImpl<>(paged, PageRequest.of(page, size), results.size());
    }

}


