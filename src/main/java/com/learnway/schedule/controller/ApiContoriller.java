package com.learnway.schedule.controller;


import com.learnway.member.domain.Member;
import com.learnway.member.service.CustomUserDetails;
import com.learnway.schedule.service.ApiService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class ApiContoriller {
	
	@Autowired
	private ApiService apiService;
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/weeklySummary")
    public ResponseEntity<Map<String, String>> weeklySummary(@RequestParam("currentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentDate
    		 					,Authentication authentication ) {
		
		Member member = null;
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            member = user.getMember();
        }
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

	    String weekRange = startOfWeek.format(DateTimeFormatter.ofPattern("MM.dd")) + " - " + endOfWeek.format(DateTimeFormatter.ofPattern("MM.dd"));

	    String summary = apiService.weeklySummary(member.getId(), startOfWeekDateTime, endOfWeekDateTime);

	    Map<String, String> response = new HashMap<>();
	    response.put("summary", summary);
	    response.put("weekRange", weekRange);

	    return ResponseEntity.ok(response);
    }
}