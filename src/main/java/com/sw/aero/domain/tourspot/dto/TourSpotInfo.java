package com.sw.aero.domain.tourspot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourSpotInfo {
    private String address;
    private Double mapX;
    private Double mapY;
    private String imageUrl;
}
