package com.tubes_impal.repos;

import com.tubes_impal.entity.CourierDailyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourierDailyStatsRepository extends JpaRepository<CourierDailyStats, Integer> {
    
    Optional<CourierDailyStats> findByCourierIdAndDate(Integer courierId, LocalDate date);
    
    List<CourierDailyStats> findByCourierId(Integer courierId);
    
    List<CourierDailyStats> findByDate(LocalDate date);
    
    List<CourierDailyStats> findByCourierIdAndDateBetween(Integer courierId, LocalDate startDate, LocalDate endDate);
}
