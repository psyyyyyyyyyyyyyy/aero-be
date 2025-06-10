package com.sw.aero.domain.aicourse.controller;

import com.sw.aero.domain.aicourse.dto.AiCourseDetailResponse;
import com.sw.aero.domain.aicourse.dto.AiTravelRequestDto;
import com.sw.aero.domain.aicourse.dto.TravelPlanDTO;
import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.aicourse.service.AiPromptBuilder;
import com.sw.aero.domain.aicourse.service.GptTravelPlanService;
import com.sw.aero.domain.course.repository.CourseLikeRepository;
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

    // ID로 AiCourse 조회
    @GetMapping("/{id}")
    public ResponseEntity<AiCourseDetailResponse> getAiCourseById(@PathVariable Long id) {
        AiCourse course = gptTravelPlanService.getCourseById(id);
        long likeCount = courseLikeRepository.countByAiCourse(course);
        return ResponseEntity.ok(AiCourseDetailResponse.from(course, likeCount));
    }

}
