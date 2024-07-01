package com.learnway.schedule.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyAchieveDto {
	
	private String DailyAchieveId;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime date;
	private double avgAchieveRate;
}
