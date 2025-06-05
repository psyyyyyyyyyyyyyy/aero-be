package com.sw.aero.domain.aicourse.controller;

import com.sw.aero.domain.aicourse.dto.AiTravelRequestDto;
import com.sw.aero.domain.aicourse.dto.TravelPlanDTO;
import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.aicourse.service.AiPromptBuilder;
import com.sw.aero.domain.aicourse.service.GptTravelPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/plans")
public class TravelPlanController {

    private final GptTravelPlanService gptTravelPlanService;
    private final AiPromptBuilder aiPromptBuilder;

    @PostMapping("/generate")
    public ResponseEntity<AiCourse> generateAndSave(@RequestBody AiTravelRequestDto requestDto) {
        String prompt = aiPromptBuilder.buildPrompt(requestDto);
        TravelPlanDTO dto = gptTravelPlanService.getTravelPlanFromGPT(prompt);
        AiCourse saved = gptTravelPlanService.saveAndReturnEntity(dto);
        return ResponseEntity.ok(saved);
    }
}
