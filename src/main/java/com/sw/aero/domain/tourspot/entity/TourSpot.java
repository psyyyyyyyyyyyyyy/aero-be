package com.sw.aero.domain.tourspot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;         // 관광지 이름
    private String address;       // 주소
    private String areaCode;      // 지역 코드 (lDongRegnCd)
    private String categoryCode;  // 분류체계 코드 (lclsSystm1)
    private Long contentId;     // 관광지 고유 ID
    private String firstImage;   // 이미지 url
    private String firstImage2;
    private String address2;         // addr2
    private String cat1;             // cat1
    private String cat2;             // cat2
    private String cat3;             // cat3
    private String contentTypeId;    // contenttypeid
    private String createdTime;      // createdtime
    private String copyrightDivCode; // cpyrhtDivCd
    private String mapX;             // mapx
    private String mapY;             // mapy
    private String mLevel;           // mlevel
    private String modifiedTime;     // modifiedtime
    private String sigunguCode;      // sigungucode
    private String tel;              // tel
    private String zipCode;          // zipcode
    private String lDongSignguCd;    // lDongSignguCd
    private String categoryCode2;    // lclsSystm2
    private String categoryCode3;    // lclsSystm3


    // 장애인 편의시설 정보
    private String parking;
    private String wheelchair;
    private String restroom;
    private String elevator;
    private String stroller;
    private String lactationroom;
    private String guidesystem;
    private String helpdog;
    private String braileblock;
    private String signguide;
    private String videoguide;
    private String audioguide;
    @Column(name = "exit_info")
    private String exit;
    private String route;
    private String publictransport;
    private String ticketoffice;
    private String promotion;
    private String auditorium;
    private String room;
    private String handicapetc;
    private String guidehuman;
    private String bigprint;
    private String brailepromotion;
    private String blindhandicapetc;
    private String hearingroom;
    private String hearinghandicapetc;
    private String babysparechair;
    private String infantsfamilyetc;

}
