package com.learnway.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learnway.global.domain.MaterialRepository;
import com.learnway.global.domain.StudywayRepository;
import com.learnway.global.domain.SubjectRepository;
import com.learnway.schedule.domain.DailyAchieve;
import com.learnway.schedule.domain.DailyAchieveRepository;
import com.learnway.schedule.domain.Progress;
import com.learnway.schedule.domain.ProgressRepository;
import com.learnway.schedule.domain.Schedule;
import com.learnway.schedule.domain.ScheduleRepository;
import com.learnway.schedule.dto.DailyAchieveDto;
import com.learnway.schedule.dto.ProgressDto;
import com.learnway.schedule.dto.ScheduleDto;

import jakarta.transaction.Transactional;

@Service
public class ScheduleService {
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@Autowired
	private DailyAchieveRepository dailyAchieveRepository;
	
	@Autowired
    private StudywayRepository studywayRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired 
    private ProgressRepository progressRepository;

	// 스케쥴 추가하기
    	@Transactional
		public void add(ScheduleDto dto) {

			Schedule schedule = new Schedule();
			schedule.setStartTime(dto.getStartTime());
			schedule.setEndTime(dto.getEndTime());
			schedule.setStudywayId(studywayRepository.findById(dto.getStudywayId())
			        .orElseThrow(() -> new RuntimeException("Studyway not found")));
			schedule.setSubjectId(subjectRepository.findById(dto.getSubjectId())
			        .orElseThrow(() -> new RuntimeException("Subject not found")));
			
			//Progress 엔티티 생성 및 설정
			List<Progress> progresses = new ArrayList<>();
			for (ProgressDto progressDto : dto.getProgresses()) {
	            Progress progress = new Progress();
	            progress.setMaterialId(materialRepository.findById(progressDto.getMaterialId())
	                    .orElseThrow(() -> new RuntimeException("Material not found")));	            
	            progress.setProgress(progressDto.getProgress());
	            progress.setScheduleId(schedule);
	            progresses.add(progress);
	        }
			schedule.setProgresses(progresses);
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
			schedule.setStudywayId(studywayRepository.findById(dto.getStudywayId())
			        .orElseThrow(() -> new RuntimeException("Studyway not found")));

			
			scheduleRepository.save(schedule);
		}
		
		//일일 스케쥴 가져오기
		public Optional<Schedule> getDetail(Long id){
			return scheduleRepository.findById(id);
		}
		
		@Transactional
		//일정 전부 변경하기
		public void updateSchedule(ScheduleDto dto) {
		    Optional<Schedule> existingSchedule = scheduleRepository.findById(dto.getScheduleId());
		    if (existingSchedule.isPresent()) {
		        Schedule schedule = existingSchedule.get();
		        // DTO의 값으로 schedule 객체 업데이트
		        schedule.setStartTime(dto.getStartTime());
		        schedule.setEndTime(dto.getEndTime());
		        schedule.setStudywayId(studywayRepository.findById(dto.getStudywayId())
				        .orElseThrow(() -> new RuntimeException("Studyway not found")));
				schedule.setSubjectId(subjectRepository.findById(dto.getSubjectId())
				        .orElseThrow(() -> new RuntimeException("Subject not found")));
				
				//Progress 엔티티 생성 및 설정
				List<Progress> progresses = new ArrayList<>();
				for (ProgressDto progressDto : dto.getProgresses()) {
		            Progress progress;
		            if (progressDto.getProgressId() != null) {
		                // 기존의 Progress 엔티티 조회
		                progress = progressRepository.findById(progressDto.getProgressId())
		                        .orElseThrow(() -> new RuntimeException("Progress not found"));
		            } else {
		                // 새로운 Progress 엔티티 생성
		                progress = new Progress();
		            }
		            progress.setMaterialId(materialRepository.findById(progressDto.getMaterialId())
		                    .orElseThrow(() -> new RuntimeException("Material not found")));
		            progress.setProgress(progressDto.getProgress());
		            progress.setScheduleId(schedule);
		            progress.setAchieveRate(progressDto.getAchieveRate());
		            progresses.add(progress);
		        }
				
				schedule.setProgresses(progresses);
				scheduleRepository.save(schedule);
		        
		    } else {
		        throw new RuntimeException("일정을 찾을 수 없습니다.");
		    }
		}
		
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
		
		//하루 평균 달성율 구하기
		public double DailyAvgAchieve(LocalDateTime dateTime) {
		    LocalDateTime endTime = dateTime.plusDays(1).minusSeconds(1); //;
		    List<Schedule> schedules = scheduleRepository.findByStartTimeBetween(dateTime, endTime);
		    
		    if (schedules.isEmpty()) {
		        return 0.0;
		    }
		    
		    double totalAchieveRate = 0.0;
		    int totalProgresses = 0;
		    
		    for (Schedule schedule : schedules) {
		    	List<Progress> progresses = schedule.getProgresses();
		    	for(Progress progress : progresses) {
		        totalAchieveRate += progress.getAchieveRate();
		        totalProgresses++;
		      }
		    }
		    
		    if (totalProgresses == 0) {
		        return 0.0;
		    }
		    
		    return totalAchieveRate / schedules.size();
		}
		
		//한 일정 달성율 구하기
		public double scheduleAchieveRate(Long id) {
			Schedule schedule = scheduleRepository.findById(id).orElseThrow(() 
					-> new IllegalArgumentException("해당 일정이 존재하지 않습니다. id: " + id));
			
			List<Progress> progresses = schedule.getProgresses();
			
			if(progresses.isEmpty()) {
				return 0.0;
			}
			
			double totalAchieveRate = 0.0;
			int totalProgresses = 0;
			double scheduleAchieveRate = 0.0;
			
			for(Progress progress : progresses) {
				totalAchieveRate += progress.getAchieveRate();
				totalProgresses++;
			}
			
			ScheduleDto dto = new ScheduleDto();
			scheduleAchieveRate = totalAchieveRate / progresses.size();
			dto.setScheduleAchieveRate(scheduleAchieveRate); 
	
			return scheduleAchieveRate;
		
		}
		
		
}
