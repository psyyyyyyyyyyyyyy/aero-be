package com.sw.aero.domain.tourspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.sw.aero.domain.tourspot.entity.TourSpot;
import java.util.Optional;

public interface TourSpotRepository extends JpaRepository<TourSpot, Long>, JpaSpecificationExecutor<TourSpot> {
    Optional<TourSpot> findByContentId(Long contentId);
}
