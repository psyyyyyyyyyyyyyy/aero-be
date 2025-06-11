package com.sw.aero.domain.travellog.repository;

import com.sw.aero.domain.travellog.entity.TravelLog;
import com.sw.aero.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelLogRepository extends JpaRepository<TravelLog, Long> {
    List<TravelLog> findAllByUser(User user);

    void deleteAllByUser(User user);
}
