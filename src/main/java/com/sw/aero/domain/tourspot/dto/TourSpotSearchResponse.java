package com.sw.aero.domain.tourspot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TourSpotSearchResponse {
    private Long id;
    private String title;
    private String address;
    private String latitude;
    private String longitude;
}
