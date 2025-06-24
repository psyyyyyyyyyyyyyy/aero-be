package com.sw.aero.domain.aicourse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.aero.domain.aicourse.dto.TravelPlanDTO;
import com.sw.aero.domain.aicourse.entity.AiCourse;
import com.sw.aero.domain.aicourse.entity.AiDetailSchedule;
import com.sw.aero.domain.aicourse.repository.AiCourseRepository;
import com.sw.aero.domain.tourspot.dto.TourSpotInfo;
import com.sw.aero.domain.tourspot.service.TourSpotInfoService;
import com.sw.aero.domain.user.entity.User;
import com.sw.aero.domain.user.repository.UserRepository;
import com.sw.aero.global.gpt.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private final TourSpotInfoService tourSpotInfoService;

    @Value("${tour.api.key}")
    private String tourApiKey;

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
                .user(user)
                .build();

        List<AiDetailSchedule> details = new ArrayList<>();

        for (TravelPlanDTO.ScheduleDTO schedule : dto.getSchedules()) {
            for (TravelPlanDTO.DetailDTO detail : schedule.getDetails()) {

                // 🟡 GPT에서 받은 place 이름 확인
                System.out.println("📍 GPT 응답 place: " + detail.getPlace());

                // 🟡 실제 호출할 searchKeyword2 API URL 확인
                String keyword = detail.getPlace();
                String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
                String url = "https://apis.data.go.kr/B551011/KorWithService2/searchKeyword2" +
                        "?serviceKey=" + tourApiKey +
                        "&MobileOS=ETC&MobileApp=aero" +
                        "&arrange=A&numOfRows=1&pageNo=1&_type=json&keyword=" + encodedKeyword;
                System.out.println("🔍 API 요청 URL: " + url);

                // 🔍 관광지 정보 조회
                TourSpotInfo info = tourSpotInfoService.findTourSpotByName(detail.getPlace());
                if (info == null) {
                    System.out.println("❌ 검색 실패 - 제외된 관광지: " + detail.getPlace());
                    continue;
                }

                AiDetailSchedule entity = AiDetailSchedule.builder()
                        .day(schedule.getDay())
                        .time(detail.getTime())
                        .place(detail.getPlace())
                        .placeId(detail.getPlaceId())
                        .address(info.getAddress())
                        .mapX(info.getMapX())
                        .mapY(info.getMapY())
                        .imageUrl(info.getImageUrl())
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
