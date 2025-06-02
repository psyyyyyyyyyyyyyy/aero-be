package com.sw.aero.domain.tourspot.entity;

import jakarta.persistence.*;
import lombok.*;
import com.sw.aero.domain.user.entity.User;

@Entity
@Table(name = "tour_spot_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "tour_spot_id"})
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourSpotLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_spot_id")
    private TourSpot tourSpot;
}