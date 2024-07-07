package com.learnway.schedule.controller;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learnway.schedule.domain.DailyAchieve;
import com.learnway.schedule.domain.DailyAchieveRepository;
import com.learnway.schedule.domain.Progress;
import com.learnway.schedule.domain.Schedule;
import com.learnway.schedule.domain.ScheduleRepository;
import com.learnway.schedule.dto.DailyAchieveDto;
import com.learnway.schedule.dto.ScheduleDto;
import com.learnway.schedule.service.ScheduleService;


@RestController
@RequestMapping("/api/schedule")
public class ScheduleRestController {
	
	private static final Logger log = LoggerFactory.getLogger(ScheduleRestController.class);
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@Autowired
	private DailyAchieveRepository dailyAchieveRepository;
	
	//일일 달성율이 담긴 월간 일정표 불러오기
	@GetMapping("/getMonthlyAchievement")
	public ResponseEntity<List<DailyAchieveDto>> getMonthlyAchievement(@RequestParam("year") int year, @RequestParam("month") int month
																		,@AuthenticationPrincipal UserDetails user){
		
		String memberId = user.getUsername();
		LocalDateTime start = LocalDateTime.of(year, month, 1, 6, 0);
	    LocalDateTime end = start.plusMonths(1);
	    
	    List<DailyAchieveDto> dailyAchieves = scheduleService.AchieveList(start, end, memberId);
	    
	    return ResponseEntity.ok(dailyAchieves);
		
	}
	
	//날짜에 따라 찾기 기준시간 오전6시 ~ 다음날 오전6시
	@GetMapping("findByDate")
	public ResponseEntity<List<Map<String,Object>>> findByDate(@RequestParam("date") String dateStr,@AuthenticationPrincipal UserDetails user){
		
		String memberId = user.getUsername();
		
		try {
			
			LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
	        LocalDateTime startDateTime = date.atTime(6, 0);
	        LocalDateTime endDateTime = startDateTime.plusDays(1).minusSeconds(1);
	        
	        List<Schedule> schedules = scheduleRepository.findByMemberIdAndStartTimeBetween(memberId,startDateTime, endDateTime);
	        
	        List<Map<String, Object>> responseData = new ArrayList<>();
	        
	        for (Schedule schedule : schedules) {
	            List<Progress> progresses = schedule.getProgresses();
	            for (Progress progress : progresses) {
	            	Map<String, Object> progressData = new HashMap<>();
	            	progressData.put("id", progress.getProgressId());
	                progressData.put("studyway", schedule.getStudywayId().getName());
	                progressData.put("subject", schedule.getSubjectId().getName());
	                progressData.put("material", progress.getMaterialId().getName());
	                progressData.put("achieveRate", progress.getAchieveRate());
	                progressData.put("progress", progress.getProgress());
	                responseData.add(progressData);
	            }
	        }
	       
	     // 해당 날짜와 멤버 ID로 DailyAchieve 조회
	        Optional<DailyAchieve> achieve = dailyAchieveRepository.findByDateAndMemberId(startDateTime, memberId);
	       
	       if(achieve.isPresent()) {
	    	    Map<String, Object> achieveData = new HashMap<>();
	    	    achieveData.put("avgAchieveRate", achieve.get().getAvgAchieveRate());
	    	    achieveData.put("DailyAchieveId", achieve.get().getDailyAchieveId());
	    	    achieveData.put("date", achieve.get().getDate());
	            responseData.add(achieveData);
	        }
	        return ResponseEntity.ok(responseData);
			
		} catch (DateTimeParseException e) {
	        return ResponseEntity.badRequest().build();
	    }
	}
	
	
	//주간 일정 전부 불러오기
	@GetMapping("/findAll")
	public List<Map<String,Object>> weekScheduleList(@AuthenticationPrincipal UserDetails user) {
		
		String memberId = user.getUsername();
		
		List<Schedule> scheduleList = scheduleService.findAllByMemberId(memberId);
		List<Map<String,Object>> eventList = new ArrayList<>();
		
		for (Schedule schedule : scheduleList) {
	        Map<String, Object> event = new HashMap<>();
	        event.put("id", schedule.getScheduleId());
	        event.put("start", schedule.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
	        event.put("end", schedule.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
	        event.put("title", schedule.getStudywayId().getName());
	        event.put("subject", schedule.getSubjectId().getName());
	        event.put("studyway", schedule.getStudywayId().getName());
	        event.put("studywayId", schedule.getStudywayId().getStudywayCode());
	        
	        double scheduleAchieveRate = scheduleService.scheduleAchieveRate(schedule.getScheduleId());
	        event.put("scheduleAchieveRate", scheduleAchieveRate);
	        

	        List<Map<String, Object>> progressList = new ArrayList<>();
	        List<Progress> progresses = schedule.getProgresses();
	        for (int i = 0; i < progresses.size(); i++) {
	        	Progress progress = progresses.get(i);
	        	Map<String, Object> progressData = new HashMap<>();
	            progressData.put("id", progress.getProgressId());
	            progressData.put("material", progress.getMaterialId().getName());
	            progressData.put("achieveRate", progress.getAchieveRate());
	            progressData.put("progress", progress.getProgress());
	            
	            // 첫 번째 progress만 event에 추가
	            if (i == 0) {
	            event.put("subTitle", progress.getProgress());
	            
	            progressList.add(progressData);
	            }
	            
	            
	        }

	        event.put("progresses", progressList);
	        eventList.add(event);
	    }
		
		return eventList;
	}

	
	//1개의 일정 내역 불러오기(수정모달용)
	@GetMapping("/getDetail")
	public Map<String,Object> getDetail(@RequestParam("id") Long id,ScheduleDto dto,@AuthenticationPrincipal UserDetails user) {
			
			String memberId = user.getUsername();
			dto.setMemberId(memberId);
			
			Optional<Schedule> detail = scheduleService.getDetail(id,memberId);
			Map<String,Object> detailList = new HashMap<>();
			
			if (detail.isPresent()) {
		        Schedule schedule = detail.get();
		        List<Progress> progresses = schedule.getProgresses();

		        detailList.put("startTime", schedule.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
		        detailList.put("endTime", schedule.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
		        detailList.put("id", schedule.getScheduleId());
		        detailList.put("studyway", schedule.getStudywayId());
		        detailList.put("subject", schedule.getSubjectId());
		        

		        List<Map<String, Object>> progressList = new ArrayList<>();
		        for (Progress progress : progresses) {
		            Map<String, Object> progressData = new HashMap<>();
		            progressData.put("progressId", progress.getProgressId());
		            progressData.put("material", progress.getMaterialId());
		            progressData.put("achieveRate", progress.getAchieveRate());
		            progressData.put("progress", progress.getProgress());
		            progressList.add(progressData);
		        }

		        detailList.put("progresses", progressList);
		    }
	
			return detailList;
		}
		
	@PostMapping("/add")
	public ResponseEntity<String> addSchedule(@RequestBody ScheduleDto dto,@AuthenticationPrincipal UserDetails user){
		
		try {
			
		String memberId = user.getUsername();
		dto.setMemberId(memberId);
				
        // 데이터 처리 로직 (DB 저장 등)
        scheduleService.add(dto);
        
        return ResponseEntity.ok("일정이 추가되었습니다.");
        
		}catch (DateTimeParseException e) {
	        return ResponseEntity.badRequest().body("잘못된 날짜/시간 형식입니다.");
	    } catch (Exception e) {
	    	log.error("일정 추가 중 오류 발생", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("일정 추가 중 오류가 발생했습니다.");
	    }
		
	}
	
	//테이블 리사이즈 또는 드래그앤드롭 이벤트시에 수정 코드
	@PatchMapping("/updateTime")
	public ResponseEntity<Map<String, String>> updateTime(@RequestBody ScheduleDto dto, @AuthenticationPrincipal UserDetails user){
	
			String memberId = user.getUsername();
			dto.setMemberId(memberId);
			scheduleService.updateScheduleTime(dto,memberId);
	
			Map<String, String> response = new HashMap<>();
		    response.put("message", "일정이 수정되었습니다");

		    return ResponseEntity.ok(response);
		
	}
	
	//일정 삭제하기
	@DeleteMapping("/deleteSchedule")
	public ResponseEntity<Map<String, String>> deleteSchedule(@RequestParam("id") Long id, @AuthenticationPrincipal UserDetails user){
			
			String memberId = user.getUsername();
			scheduleService.deleteSchedule(id,memberId);
			Map<String, String> response = new HashMap<>();
		    response.put("message", "일정이 삭제되었습니다");
		    
		    return ResponseEntity.ok(response);
	}
	
	//일정 수정하기
	@PatchMapping("/updateSchedule")
	public ResponseEntity<Map<String, String>> updateSchedule(@RequestBody ScheduleDto dto,@AuthenticationPrincipal UserDetails user){

		String memberId = user.getUsername();
		dto.setMemberId(memberId);
		
		try {
	        Optional<Schedule> existingSchedule = scheduleService.getDetail(dto.getScheduleId(),memberId);
	        if (!existingSchedule.isPresent()) {
	        	Map<String, String> response = new HashMap<>();
	    	    response.put("message", "해당 ID 일정을 찾을 수 없습니다");
	    	    
	    	    return ResponseEntity.ok(response);
	        }
	        
		
        // 데이터 처리 로직 (DB 저장 등)
        scheduleService.updateSchedule(dto,memberId);
        
        Map<String, String> response = new HashMap<>();
	    response.put("message", "일정이 성공적으로 수정되었습니다");
	    
	    return ResponseEntity.ok(response);
		
		}catch (Exception e) {
	        log.error("일정 업데이트 중 오류 발생", e);
	        Map<String, String> response = new HashMap<>();
		    response.put("message", "일정 업데이트 중 오류 발생");
		    
		    return ResponseEntity.ok(response);
	    }
	}
		
}
