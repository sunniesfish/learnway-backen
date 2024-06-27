package com.learnway.schedule.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DailyAchieveRepository extends JpaRepository<DailyAchieve, String>{
	List<DailyAchieve> findByDateBetween(LocalDateTime start,LocalDateTime end);
	
	@Query(value = "SELECT * FROM daily_achieve WHERE date >= :start AND date < :end", nativeQuery = true)
    List<DailyAchieve> findCustomDailyAchieves(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
	
	 @Query(value = "SELECT * FROM daily_achieve WHERE date >= CONCAT(:year, '-', :month, '-01 06:00:00') AND date < DATE_ADD(CONCAT(:year, '-', :month, '-01 06:00:00'), INTERVAL 1 MONTH)", nativeQuery = true)
	    List<DailyAchieve> findMonthlyAchievements(@Param("year") int year, @Param("month") int month);
}
