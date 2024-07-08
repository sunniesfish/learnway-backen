package com.learnway.schedule.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>{
	
	//달력 모든 정보 불러오기
	List<Schedule> findAllByMemberId(String memberId);
	
	Optional<Schedule> findByScheduleIdAndMemberId(Long id,String memberId);
	
	//스케쥴 기준 입력날짜 사이 일정 불러오기 
	List<Schedule> findByMemberIdAndStartTimeBetween(
			String memberId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
