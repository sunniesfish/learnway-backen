package com.learnway.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.learnway.schedule.domain.DailyAchieve;
import com.learnway.schedule.domain.DailyAchieveRepository;
import com.learnway.schedule.domain.Schedule;
import com.learnway.schedule.domain.ScheduleRepository;
import com.learnway.schedule.dto.DailyAchieveDto;
import com.learnway.schedule.dto.ScheduleDto;

@Service
public class ScheduleService {
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@Autowired
	private DailyAchieveRepository dailyAchieveRepository;

	// 스케쥴 추가하기
		public void add(ScheduleDto dto) {

			Schedule schedule = new Schedule();
			schedule.setScheduleId(dto.getScheduleId());
			schedule.setStartTime(dto.getStartTime());
			schedule.setEndTime(dto.getEndTime());
			schedule.setStudyway(dto.getStudyway());
			schedule.setMaterial(dto.getMaterial());
			schedule.setProgress(dto.getProgress());
			schedule.setSubject(dto.getSubject());
			schedule.setAchieveRate(dto.getAchieveRate());
			
			scheduleRepository.save(schedule);
		}
		
		//일정 전부 불러오기
		public List<Schedule> findAll(){
			return scheduleRepository.findAll();
		}
		
		//일정 시간만 변경하기
		public void updateScheduleTime(ScheduleDto dto) {
			
			Schedule schedule = scheduleRepository.findById(dto.getScheduleId()).orElseThrow(() 
					-> new IllegalArgumentException("해당 일정이 존재하지 않습니다. id: " + dto.getScheduleId()));
			
			schedule.setStartTime(dto.getStartTime());
			schedule.setEndTime(dto.getEndTime());
			schedule.setStudyway(dto.getStudyway());
			
			scheduleRepository.save(schedule);
		}
		
		//일일 스케쥴 가져오기
		public Optional<Schedule> getDetail(Long id){
			return scheduleRepository.findById(id);
		}
		
		//일정 전부 변경하기
		public void updateSchedule(ScheduleDto dto) {
		    Optional<Schedule> schudule = scheduleRepository.findById(dto.getScheduleId());
		    if (schudule.isPresent()) {
		        Schedule schedule = schudule.get();
		        // DTO의 값으로 schedule 객체 업데이트
		        schedule.setStartTime(dto.getStartTime());
		        schedule.setEndTime(dto.getEndTime());
		        schedule.setStudyway(dto.getStudyway());
		        schedule.setSubject(dto.getSubject());
		        schedule.setMaterial(dto.getMaterial());
		        schedule.setProgress(dto.getProgress());
		        schedule.setAchieveRate(dto.getAchieveRate());
		        
		        scheduleRepository.save(schedule);
		    } else {
		        throw new RuntimeException("일정을 찾을 수 없습니다.");
		    }
		}
		
		
//		public ScheduleDto getDetailll(Long id) throws DataNotException {
//			
//			Optional<Schedule> schedule = scheduleRepository.findById(id);
//			
//			if (schedule.isPresent()) {
//		        Schedule updateschedule = schedule.get();
//		        ScheduleDto dto = convertDTO(updateschedule);
//
//		        return dto;
//		    } else {
//		        throw new DataNotException("schedule not found");
//		    }
//		
//		}
//		
//		private ScheduleDto convertDTO(Schedule schedule) {
//			
//			ScheduleDto dto = new ScheduleDto();
//			
//			dto.setScheduleId(schedule.getScheduleId());
//			dto.setStartTime(schedule.getStartTime());
//			dto.setEndTime(schedule.getEndTime());
//			dto.setStudyway(schedule.getStudyway());
//			dto.setSubject(schedule.getSubject());
//			dto.setProgress(schedule.getProgress());
//			dto.setMaterial(schedule.getMaterial());
//			dto.setAchieveRate(schedule.getAchieveRate());
//			
//			return dto;
//		}
		
		//스케쥴 삭제하기
		public void deleteSchedule(Long id) {
			scheduleRepository.deleteById(id);
		}
		
		//일달성율 평균 구하기
		public List<DailyAchieveDto> AchieveList(LocalDateTime start, LocalDateTime  end) {
			
			List<DailyAchieve> dailyAchieves = dailyAchieveRepository.findByDateBetween(start, end);
			List<DailyAchieveDto> result = new ArrayList<>();
			
			LocalDateTime current = start;
	        while (current.isBefore(end)) {
	        	String id = generateDailyAchieveId(current);
	            DailyAchieve dailyAchieve = findOrCreateDailyAchieve(id, current);
	            
	            DailyAchieveDto dto = new DailyAchieveDto();
	            dto.setDailyAchieveId(dailyAchieve.getDailyAchieveId());
	            dto.setDate(dailyAchieve.getDate());
	            dto.setAvgAchieveRate(dailyAchieve.getAvgAchieveRate());
	            
	            result.add(dto);
	            current = current.plusDays(1);
	        }
	        
	        return result;
		
		}
		
		public String generateDailyAchieveId(LocalDateTime dateTime) {
	        // ID 형식: YYYYMMDD (날짜는 오전 6시 기준)
	        return dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	    }
		
		private DailyAchieve findOrCreateDailyAchieve(String id, LocalDateTime dateTime) {
			
	       
	        Optional<DailyAchieve> achieve = dailyAchieveRepository.findById(id);
	        if(achieve.isPresent()) {
	        	DailyAchieve oldAchieve = achieve.get();
	        	oldAchieve.setDailyAchieveId(id);
	        	oldAchieve.setDate(dateTime);
	        	oldAchieve.setAvgAchieveRate(DailyAvgAchieve(dateTime));
		        dailyAchieveRepository.save(oldAchieve);
		        return oldAchieve;
	        	
	        }else {
	        
		        // 존재하지 않으면 새로 생성
		        DailyAchieve newAchieve = new DailyAchieve();
		        newAchieve.setDailyAchieveId(id);
		        newAchieve.setDate(dateTime);
		        newAchieve.setAvgAchieveRate(DailyAvgAchieve(dateTime));
		        dailyAchieveRepository.save(newAchieve);
		        return newAchieve;
		        }
	    }
		
		public double DailyAvgAchieve(LocalDateTime dateTime) {
		    LocalDateTime endTime = dateTime.plusDays(1).minusSeconds(1); //;
		    List<Schedule> schedules = scheduleRepository.findByStartTimeBetween(dateTime, endTime);
		    
		    if (schedules.isEmpty()) {
		        return 0.0;
		    }
		    
		    double totalAchieveRate = 0.0;
		    for (Schedule schedule : schedules) {
		        totalAchieveRate += schedule.getAchieveRate();
		    }
		    
		    return totalAchieveRate / schedules.size();
		}
		
		
}
