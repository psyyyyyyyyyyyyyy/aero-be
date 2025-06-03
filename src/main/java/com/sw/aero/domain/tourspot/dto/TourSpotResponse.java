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

    public static TourSpotResponse from(TourSpot spot, long likeCount) {
        return TourSpotResponse.builder()
                .id(spot.getId())
                .title(spot.getTitle())
                .address(spot.getAddress())
                .areaCode(spot.getAreaCode())
                .firstImage(spot.getFirstImage())
                .likeCount(likeCount)
                .build();
    }
}