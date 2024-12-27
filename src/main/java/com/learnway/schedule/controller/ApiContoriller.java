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
    public ResponseEntity<Map<String, String>> weeklySummary(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate
    		                    ,@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    		 					,Authentication authentication ) {
		
		Member member = null;
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            member = user.getMember();
        }
		// 주간 날짜 정보 수정
		
        LocalDateTime startOfWeekDateTime = startDate.plusDays(1).atTime(6, 0);
        LocalDateTime endOfWeekDateTime = endDate.plusDays(1).atTime(5, 59);

	    String weekRange = startDate.plusDays(1).format(DateTimeFormatter.ofPattern("MM.dd")) + " - " + endDate.format(DateTimeFormatter.ofPattern("MM.dd"));
	    String summary = apiService.weeklySummary(member.getId(), startOfWeekDateTime, endOfWeekDateTime);

	    Map<String, String> response = new HashMap<>();
	    response.put("summary", summary);
	    response.put("weekRange", weekRange);

	    return ResponseEntity.ok(response);
    }
}