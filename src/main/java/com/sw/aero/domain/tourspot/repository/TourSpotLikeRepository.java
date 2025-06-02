package com.sw.aero.domain.tourspot.repository;

import com.sw.aero.domain.tourspot.entity.TourSpotLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TourSpotLikeRepository extends JpaRepository<TourSpotLike, Long> {
    boolean existsByUserIdAndTourSpotId(Long userId, Long tourSpotId);
    long countByTourSpotId(Long tourSpotId);
    Optional<TourSpotLike> findByUserIdAndTourSpotId(Long userId, Long tourSpotId);
}