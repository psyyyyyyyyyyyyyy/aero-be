package com.sw.aero.domain.tourspot.repository;

import com.sw.aero.domain.tourspot.entity.TourSpot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TourSpotRepository extends JpaRepository<TourSpot, Long>, JpaSpecificationExecutor<TourSpot> {
    Optional<TourSpot> findByContentId(Long contentId);

    //제목에 keyword가 포함된 관광지 검색
    Page<TourSpot> findByTitleContaining(String keyword, Pageable pageable);

}
