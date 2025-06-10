package com.sw.aero.domain.aicourse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.aero.domain.aicourse.dto.TravelPlanDTO;
import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.aicourse.entity.AiDetailSchedule;
import com.sw.aero.domain.aicourse.repository.AiCourseRepository;
import com.sw.aero.domain.user.entity.User;
import com.sw.aero.domain.user.repository.UserRepository;
import com.sw.aero.global.gpt.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GptTravelPlanService {

    private final OpenAiService openAiService;
    private final AiPromptBuilder aiPromptBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AiCourseRepository aiCourseRepository;
    private final UserRepository userRepository;

    public TravelPlanDTO getTravelPlanFromGPT(String prompt) {
        try {
            String gptResponse = openAiService.getChatResponse(prompt);
            return objectMapper.readValue(gptResponse, TravelPlanDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("GPT 응답 파싱 실패: " + e.getMessage(), e);
        }
    }

    public AiCourse saveAndReturnEntity(TravelPlanDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        AiCourse course = AiCourse.builder()
                .title(dto.getTitle())
                .theme(dto.getTheme())
                .allow(dto.isAllow())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .people(dto.getPeople())
                .user(user) // 사용자 설정
                .build();

        List<AiDetailSchedule> details = new ArrayList<>();
        for (TravelPlanDTO.ScheduleDTO schedule : dto.getSchedules()) {
            for (TravelPlanDTO.DetailDTO detail : schedule.getDetails()) {
                AiDetailSchedule entity = AiDetailSchedule.builder()
                        .day(schedule.getDay())
                        .time(detail.getTime())
                        .place(detail.getPlace())
                        .placeId(detail.getPlaceId())
                        .address(detail.getAddress())
                        .imageUrl(detail.getImageUrl())
                        .description(detail.getDescription())
                        .barrierFree(detail.getBarrierFree())
                        .aiCourse(course)
                        .build();
                details.add(entity);
            }
        }

        course.setSchedules(details);
        return aiCourseRepository.save(course);
    }

    public AiCourse getCourseById(Long id) {
        return aiCourseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 AiCourse가 존재하지 않습니다: id=" + id));
    }

}
