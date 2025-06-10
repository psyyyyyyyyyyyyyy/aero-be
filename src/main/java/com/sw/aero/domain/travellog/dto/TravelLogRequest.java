package com.sw.aero.domain.travellog.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelLogRequest {
    private String address;
    private String imageUrl;
    private String content;
}
