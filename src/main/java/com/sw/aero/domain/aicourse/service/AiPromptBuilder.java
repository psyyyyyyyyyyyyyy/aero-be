package com.sw.aero.domain.aicourse.service;

import com.sw.aero.domain.aicourse.dto.AiTravelRequestDto;
import org.springframework.stereotype.Component;

@Component
public class AiPromptBuilder {

    public String buildPrompt(AiTravelRequestDto dto) {
        return """
                너는 베리어프리 여행 코스를 생성해주는 AI야.
                
                반드시 아래 조건을 정확히 지켜서 JSON 형식의 응답을 생성해.
                
                 전체 조건:
                - 응답은 반드시 `JSON 형식`만 포함하고, **설명 문구는 절대 포함하지 마**
                - 각 관광지는 반드시 `베리어프리` 장소여야 하며, `barrierFree` 객체는 실제 해당되는 항목만 포함해
                - 모든 place 이름은 반드시 대한민국의 실제 관광지명이어야 하며, 한국관광공사의 searchKeyword2 API에서 검색 가능한 정식 명칭만 사용해.
                - description은 2줄 이상
                
                 barrierFree 항목 목록:
                  - parking : 장애인 주차장
                  - wheelchair : 휠체어 대여 가능 여부
                  - restroom : 장애인 화장실
                  - elevator : 엘리베이터 여부
                  - publictransport : 휠체어 접근 가능 여부
                  - guidehuman : 안내직원 여부
                
                 일정 구성:
                - 사용자가 입력한 `startDate`부터 `endDate`까지 날짜 수만큼 `schedules`에 day를 생성해야 해
                - 예: startDate: 2025-06-19, endDate: 2025-06-21이면 반드시 day 1, day 2, day3 모두 포함해야 함
                - 각 day에는 `최소 5개 이상의 관광지`를 포함해야 함
                
                 예시 응답 형식:
                {
                  "title": "서울 힐링 여행",
                  "theme": "자연관광",
                  "startDate": "사용자가 입력한 startDate",
                  "endDate": "사용자가 입력한 endDate",
                  "people": "혼자서",
                  "allow": true,
                  "schedules": [
                    {
                      "day": 1,
                      "details": [
                        {
                          "time": "10:00",
                          "place": "경복궁",
                          "placeId": "1",
                          "description": "고궁의 아름다움과 한국 역사를 느낄 수 있는 대표 명소입니다.",
                          "barrierFree": {
                            "wheelchair": "휠체어 대여 가능",
                            "restroom": "장애인 화장실 있음",
                            "publictransport": "출입구까지 단차가 없어 휠체어 접근 가능함",
                            "guidehuman" : "안내데스크 있음(1층 로비)"
                          }
                        },
                        ...
                      ]
                    },
                    {
                      "day": 2,
                      "details": [
                        ...
                      ]
                    }
                  ]
                }
                
                참고사항:
                - 지역: %s
                - people: %s
                - startDate: %s
                - endDate: %s
                - theme: %s
                - 일정 스타일: %s 일정으로 구성 (빡빡하게 or 널널하게)
                """.formatted(
                dto.getRegion(),
                dto.getPeople(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getTheme(),
                dto.getPace()
        );
    }
}
