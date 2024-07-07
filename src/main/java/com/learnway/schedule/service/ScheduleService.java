package com.learnway.schedule.service;

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

	    // 스케쥴 추가하기 (주간)
    	@Transactional
		public void add(ScheduleDto dto) {

			Schedule schedule = new Schedule();
			schedule.setStartTime(dto.getStartTime());
			schedule.setEndTime(dto.getEndTime());
			schedule.setStudywayId(studywayRepository.findById(dto.getStudywayId())
			        .orElseThrow(() -> new RuntimeException("Studyway not found")));
			schedule.setSubjectId(subjectRepository.findById(dto.getSubjectId())
			        .orElseThrow(() -> new RuntimeException("Subject not found")));
			schedule.setMemberId(dto.getMemberId());
			
			//Progress 엔티티 생성 및 설정
			List<Progress> progresses = new ArrayList<>();
			for (ProgressDto progressDto : dto.getProgresses()) {
	            Progress progress = new Progress();
	            progress.setMaterialId(materialRepository.findById(progressDto.getMaterialId())
	                    .orElseThrow(() -> new RuntimeException("Material not found")));	            
	            progress.setProgress(progressDto.getProgress());
	            progress.setScheduleId(schedule);
	            progress.setMemberId(schedule.getMemberId());
	            progresses.add(progress);
	        }
			schedule.setProgresses(progresses);
			scheduleRepository.save(schedule);
		}
		
		//일정 전부 불러오기
		public List<Schedule> findAllByMemberId(String memberId){
			return scheduleRepository.findAllByMemberId(memberId);
		}
		
		@Transactional
		//일정 시간만 변경하기 (주간)
		public void updateScheduleTime(ScheduleDto dto,String memberId) {
			
			Schedule schedule = scheduleRepository.findById(dto.getScheduleId()).orElseThrow(() 
					-> new IllegalArgumentException("해당 일정이 존재하지 않습니다. id: " + dto.getScheduleId()));
			
			// 멤버 ID 확인
	        if (!schedule.getMemberId().equals(dto.getMemberId())) {
	            throw new RuntimeException("해당 일정은 다른 멤버의 일정입니다.");
	        }
	        
			schedule.setStartTime(dto.getStartTime());
			schedule.setEndTime(dto.getEndTime());
			schedule.setStudywayId(studywayRepository.findById(dto.getStudywayId())
			        .orElseThrow(() -> new RuntimeException("Studyway not found")));

			
			scheduleRepository.save(schedule);
		}
		
		//일일 스케쥴 가져오기
		public Optional<Schedule> getDetail(Long id,String memberId){
			return scheduleRepository.findByScheduleIdAndMemberId(id,memberId);
		}
		
		@Transactional
		//일정 전부 변경하기 (주간)
		public void updateSchedule(ScheduleDto dto,String memberId) {
			
			
			
		    Optional<Schedule> existingSchedule = scheduleRepository.findByScheduleIdAndMemberId(dto.getScheduleId(),memberId);
		    if (existingSchedule.isPresent()) {
		        Schedule schedule = existingSchedule.get();
		        
		        // 멤버 ID 확인
		        if (!schedule.getMemberId().equals(dto.getMemberId())) {
		            throw new RuntimeException("해당 일정은 다른 멤버의 일정입니다.");
		        }
		        
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
		public void deleteSchedule(Long id,String memberId) {
			// 연관된 Progress 엔티티 먼저 삭제
			Schedule schedule = scheduleRepository.findByScheduleIdAndMemberId(id,memberId)
		            .orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다. id: " + id));
		    
		    // 연관된 Progress 엔티티 먼저 삭제
		    progressRepository.deleteByScheduleId(schedule);
			scheduleRepository.deleteById(id);
		}
		
		//일달성율 평균 리스트 조회 
		public List<DailyAchieveDto> AchieveList(LocalDateTime start, LocalDateTime  end, String memberId) {
			
			List<DailyAchieve> dailyAchieves = dailyAchieveRepository.findByMemberIdAndDateBetween(memberId, start, end);
			List<DailyAchieveDto> result = new ArrayList<>();
			
			LocalDateTime current = start;
	        while (current.isBefore(end)) {
	            DailyAchieve dailyAchieve = findOrCreateDailyAchieve(current,memberId);
	            
	            DailyAchieveDto dto = new DailyAchieveDto();
	            dto.setDailyAchieveId(dailyAchieve.getDailyAchieveId());
	            dto.setDate(dailyAchieve.getDate());
	            dto.setAvgAchieveRate(dailyAchieve.getAvgAchieveRate());
	            dto.setMemberId(memberId);
	            
	            result.add(dto);
	            current = current.plusDays(1);
	        }
	        
	        return result;
		
		}

		
		private DailyAchieve findOrCreateDailyAchieve(LocalDateTime dateTime,String memberId) {
			
	       
			// 해당 날짜와 멤버 ID로 DailyAchieve 조회
		    Optional<DailyAchieve> achieve = dailyAchieveRepository.findByDateAndMemberId(dateTime, memberId);
	        
		    if(achieve.isPresent()) {
	        	return achieve.get();
	        	
	        }else {
	        
		        // 존재하지 않으면 새로 생성
		        DailyAchieve newAchieve = new DailyAchieve();
		        newAchieve.setDate(dateTime);
		        newAchieve.setAvgAchieveRate(DailyAvgAchieve(dateTime,memberId));
		        newAchieve.setMemberId(memberId);
		        dailyAchieveRepository.save(newAchieve);
		        return newAchieve;
		        }
	    }
		
		//하루 평균 달성율 구하기
		public double DailyAvgAchieve(LocalDateTime dateTime,String memberId) {
		    LocalDateTime endTime = dateTime.plusDays(1).minusSeconds(1); //;
		    List<Schedule> schedules = scheduleRepository.findByMemberIdAndStartTimeBetween(memberId,dateTime, endTime);
		    
		    if (schedules.isEmpty()) {
		        return 0.0;
		    }
		    
		    double totalAchieveRate = 0.0;
		    int totalProgresses = 0;
		    
		    for (Schedule schedule : schedules) {
		        totalAchieveRate += schedule.getScheduleAchieveRate();
		        totalProgresses++;
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
			scheduleAchieveRate = totalAchieveRate / progresses.size();
			
			ScheduleDto dto = new ScheduleDto();
			dto.setScheduleId(id);
			dto.setScheduleAchieveRate(scheduleAchieveRate); 
			schedule.setScheduleAchieveRate(scheduleAchieveRate);
			scheduleRepository.save(schedule);
	
			return scheduleAchieveRate;
		
		}
		
		
}
