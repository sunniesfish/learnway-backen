package com.learnway.schedule.controller;

import com.learnway.schedule.domain.Progress;
import com.learnway.schedule.domain.Schedule;
import com.learnway.schedule.domain.ScheduleRepository;
import com.learnway.schedule.service.ApiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<String> weeklySummary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentDate
    		 					,@AuthenticationPrincipal UserDetails user ) {
		
		String memberId = user.getUsername();
		String summary = apiService.weeklySummary(memberId,currentDate);
    	
		return ResponseEntity.ok(summary);
    }
}