package com.sw.aero.domain.tourspot.dto;

import com.sw.aero.domain.tourspot.entity.TourSpot;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TourSpotResponse {
    private Long id;
    private String title;
    private String address;
    private String areaCode;
    private String firstImage;
    private long likeCount;
    private boolean liked;
    private Long contentId;
    private String categoryCode;
    private String mapX;
    private String mapY;

    // 편의시설 정보 (String 타입)
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

    public static TourSpotResponse from(TourSpot spot, long likeCount, boolean liked) {
        return TourSpotResponse.builder()
                .id(spot.getId())
                .title(spot.getTitle())
                .address(spot.getAddress())
                .mapX(spot.getMapX())
                .mapY(spot.getMapY())
                .areaCode(spot.getAreaCode())
                .firstImage(spot.getFirstImage())
                .likeCount(likeCount)
                .liked(liked)
                .contentId(spot.getContentId())
                .categoryCode(spot.getCategoryCode())
                .parking(spot.getParking())
                .wheelchair(spot.getWheelchair())
                .restroom(spot.getRestroom())
                .elevator(spot.getElevator())
                .stroller(spot.getStroller())
                .lactationroom(spot.getLactationroom())
                .guidesystem(spot.getGuidesystem())
                .helpdog(spot.getHelpdog())
                .braileblock(spot.getBraileblock())
                .signguide(spot.getSignguide())
                .videoguide(spot.getVideoguide())
                .audioguide(spot.getAudioguide())
                .exit(spot.getExit())
                .route(spot.getRoute())
                .publictransport(spot.getPublictransport())
                .ticketoffice(spot.getTicketoffice())
                .promotion(spot.getPromotion())
                .auditorium(spot.getAuditorium())
                .room(spot.getRoom())
                .handicapetc(spot.getHandicapetc())
                .guidehuman(spot.getGuidehuman())
                .bigprint(spot.getBigprint())
                .brailepromotion(spot.getBrailepromotion())
                .blindhandicapetc(spot.getBlindhandicapetc())
                .hearingroom(spot.getHearingroom())
                .hearinghandicapetc(spot.getHearinghandicapetc())
                .babysparechair(spot.getBabysparechair())
                .infantsfamilyetc(spot.getInfantsfamilyetc())
                .build();
    }
}
