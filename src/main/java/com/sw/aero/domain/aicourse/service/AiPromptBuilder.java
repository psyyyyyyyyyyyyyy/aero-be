package com.sw.aero.domain.aicourse.service;

import com.sw.aero.domain.aicourse.dto.AiTravelRequestDto;
import org.springframework.stereotype.Component;

@Component
public class AiPromptBuilder {

    public String buildPrompt(AiTravelRequestDto dto) {
        return """
                                너는 베리어프리 여행 코스를 짜주는 AI야.
                
                                아래와 같은 JSON 구조로 응답해줘. 주석 없이, JSON만 정확히 출력해.
                                모든 관광지는 반드시 베리어프리 관광지여야 하며, barrierFree 객체에는 실제 해당되는 항목만 포함해.
                
                                [포함 가능한 barrierFree 항목 목록]
                                        - parking
                                        - wheelchair
                                        - restroom
                                        - elevator
                                        - stroller
                                        - lactationroom
                                        - guidesystem
                                        - helpdog
                                        - braileblock
                                        - signguide
                                        - videoguide
                                        - audioguide
                                        - exit
                                        - route
                                        - publictransport
                                        - ticketoffice
                                        - promotion
                                        - auditorium
                                        - room
                                        - handicapetc
                                        - guidehuman
                                        - bigprint
                                        - brailepromotion
                                        - blindhandicapetc
                                        - hearingroom
                                        - hearinghandicapetc
                                        - babysparechair
                                        - infantsfamilyetc
                
                                title은 지역, 테마, 동행자, 일정 정보를 반영해서 자연스럽고 매력적으로 생성해줘.
                                schedules 배열은 startDate부터 endDate까지의 날짜를 기준으로 생성하고,
                                하루에 최소 2개 이상의 관광지를 포함해.
                
                                placeId는 숫자로 구성하고 응답 안에서 중복되지 않게 해줘.
                                description은 2줄 이상.
                
                                각 관광지에는 반드시 접근 가능한 실제 이미지 URL을 포함해줘.
                                imageUrl은 실제 이미지를 직접 볼 수 있는 웹 링크여야 하며, 한국 관광지라면 국내 공식 이미지로 구성해.
                
                                응답 예시는 다음과 같은 형식으로 해줘:
                                {
                                  "title": "예시 여행 제목",
                                  "theme": "숙박 | 축제/공연/행사 | 체험관광 | 음식 | 역사관광 | 레저스포츠 | 자연관광 | 쇼핑 | 문화관광 중 하나",
                                  "startDate": "2025-08-01",
                                  "endDate": "2025-08-03",
                                  "people": "혼자서 | 친구랑 | 연인과 | 가족과 중 하나",
                                  "allow": true,
                                  "schedules": [
                                    {
                                      "day": 1,
                                      "details": [
                                        {
                                          "time": "10:00",
                                          "place": "경복궁",
                                          "placeId": "1",
                                          "address": "관광지 주소",
                                          "imageUrl": "https://cdn.visitkorea.or.kr/img/call?cmd=VIEW&id=abcd1234",
                                          "description": "관광지 설명 2줄 이상",
                                          "barrierFree": {
                                          "wheelchair": "휠체어 이동 가능"
                                        }
                                        },
                                        {
                                          "time": "14:00",
                                          "place": "관광지 이름2",
                                          "placeId": "2",
                                          "address": "관광지 주소2",
                                          "imageUrl": "https://cdn.visitkorea.or.kr/img/call?cmd=VIEW&id=abcd1234",
                                          "description": "관광지 설명 2줄 이상",
                                          "barrierFree": {
                                            "wheelchair": "휠체어 이동 가능",
                                            "restroom": "장애인 화장실 있음"
                                          }
                                        }
                                      ]
                                    }
                                  ]
                                }
                
                                참고사항:
                                - 지역: %s
                                - 동행자: %s
                                - 여행 시작일: %s
                                - 여행 종료일: %s
                                - 테마: %s
                                - 일정 스타일: %s 일정으로 구성 (빡빡하게 or 널널하게)
                                - 응답은 반드시 JSON 형식만 포함하고, 설명 문구는 절대 넣지 마.
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
