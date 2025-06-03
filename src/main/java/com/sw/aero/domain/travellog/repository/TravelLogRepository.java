package com.sw.aero.domain.travellog.repository;

import com.sw.aero.domain.travellog.entity.TravelLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelLogRepository extends JpaRepository<TravelLog, Long> {
}
