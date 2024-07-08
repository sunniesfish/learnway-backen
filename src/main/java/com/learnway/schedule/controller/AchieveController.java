package com.learnway.schedule.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.learnway.schedule.domain.Progress;
import com.learnway.schedule.domain.Schedule;
import com.learnway.schedule.domain.ScheduleRepository;
import com.learnway.schedule.service.ScheduleService;


@Controller("/achieve")
public class AchieveController {
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@GetMapping("/getDaily")
	public String getDaily(@AuthenticationPrincipal UserDetails user, Long id, String currentDate, Model model){
		
		String memberId = user.getUsername();
		LocalDate date = LocalDate.parse(currentDate, DateTimeFormatter.ISO_DATE);
        LocalDateTime startDateTime = date.atTime(6, 0);
        LocalDateTime endDateTime = startDateTime.plusDays(1).minusSeconds(1);
        
        List<Schedule> schedules = scheduleRepository.findByMemberIdAndStartTimeBetween(memberId,startDateTime, endDateTime);
       
        for (Schedule schedule : schedules) {
            List<Progress> progresses = schedule.getProgresses();
            model.addAttribute("progresses_" + schedule.getScheduleId(), progresses);
        }
        model.addAttribute("schedules", schedules);
        
        return "/scheduleAchieve";

	}
	@GetMapping("/getWeekly")
	public String getWeekly(@AuthenticationPrincipal UserDetails user, Long id, LocalDate currentDate, Model model){
		
		String memberId = user.getUsername();
		
		// 주간 날짜 정보 수정
		LocalDate prevWeekSameDay = currentDate.minusWeeks(1);
		LocalDate startOfWeek = prevWeekSameDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.from(prevWeekSameDay)));
		LocalDate endOfWeek = startOfWeek.plusDays(6);
		
		if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
			startOfWeek = currentDate;
			endOfWeek = startOfWeek.plusDays(6);
		}
		
		LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay();
	    LocalDateTime endOfWeekDateTime = endOfWeek.atTime(23, 59, 59);
		
		List<Schedule> schedules = scheduleRepository.findByMemberIdAndStartTimeBetween(memberId,startOfWeekDateTime, endOfWeekDateTime);
		
		for (Schedule schedule : schedules) {
			List<Progress> progresses = schedule.getProgresses();
			model.addAttribute("progresses_" + schedule.getScheduleId(), progresses);
		}
		model.addAttribute("schedules", schedules);
		
		return "/scheduleAchieve";
		
	}

}
