package com.sw.aero.domain.aicourse.controller;

import com.sw.aero.domain.aicourse.dto.AiCourseDetailResponse;
import com.sw.aero.domain.aicourse.dto.AiTravelRequestDto;
import com.sw.aero.domain.aicourse.dto.TravelPlanDTO;
import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.aicourse.service.AiPromptBuilder;
import com.sw.aero.domain.aicourse.service.GptTravelPlanService;
import com.sw.aero.domain.course.repository.CourseLikeRepository;
import com.sw.aero.domain.user.entity.User;
import com.sw.aero.domain.user.repository.UserRepository;
import com.sw.aero.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/plans")
public class TravelPlanController {

    private final GptTravelPlanService gptTravelPlanService;
    private final AiPromptBuilder aiPromptBuilder;
    private final CourseLikeRepository courseLikeRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    // 여행 계획 생성 + 저장
    @PostMapping("/generate")
    public ResponseEntity<AiCourse> generateAndSave(
            @RequestBody AiTravelRequestDto requestDto,
            @RequestHeader("Authorization") String accessToken
    ) {
        Long userId = jwtProvider.getUserIdFromToken(accessToken.replace("Bearer ", ""));
        String prompt = aiPromptBuilder.buildPrompt(requestDto);
        TravelPlanDTO dto = gptTravelPlanService.getTravelPlanFromGPT(prompt);
        AiCourse saved = gptTravelPlanService.saveAndReturnEntity(dto, userId); // userId 넘김
        return ResponseEntity.ok(saved);
    }

    // ID로 AiCourse 조회 (토큰 없어도 가능)
    @GetMapping("/{id}")
    public ResponseEntity<AiCourseDetailResponse> getAiCourseById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String accessToken   // ✅ optional
    ) {
        User user = null;

        // 1) 토큰이 있을 때만 user 추출
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            try {
                Long userId = jwtProvider.getUserIdFromToken(accessToken.replace("Bearer ", ""));
                user = userRepository.findById(userId).orElse(null);   // 없으면 null
            } catch (Exception e) {
                // 토큰이 잘못됐으면 무시하고 비로그인 처리
                user = null;
            }
        }

        // 2) 코스·좋아요 수 조회
        AiCourse course = gptTravelPlanService.getCourseById(id);
        long likeCount = courseLikeRepository.countByAiCourse(course);

        // 3) 로그인 상태일 때만 liked 계산
        boolean liked = (user != null) &&
                courseLikeRepository.findByUserAndAiCourse(user, course).isPresent();

        // 4) DTO에 liked 포함
        return ResponseEntity.ok(AiCourseDetailResponse.from(course, likeCount, liked));
    }


}
